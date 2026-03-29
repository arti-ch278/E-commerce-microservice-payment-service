package com.artichourey.ecommerce.paymentservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;
import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.artichourey.ecommerce.paymentservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.paymentservice.repository.PaymentRepository;
import com.artichourey.ecommerce.paymentservice.service.PaymentService;
import com.artichourey.ecommerce.paymentservice.util.TransactionGenerate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public ResponsePayment createPayment(RequestPayment request) {

        log.info("Processing payment request | orderId={}, amount={}",
                request.getOrderId(), request.getAmount());

        // First-level idempotency check
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(request.getOrderId());

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            log.warn("Duplicate payment request detected (pre-check) | orderId={}, status={}",
                    request.getOrderId(), payment.getPaymentStatus());

            return mapToResponse(payment);
        }

        try {
            // Create new payment
            Payment payment = Payment.builder()
                    .orderId(request.getOrderId())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus(PaymentStatus.PENDING)
                    .transactionId(TransactionGenerate.generate())
                    .userId(request.getUserId())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            log.info("Payment created successfully | orderId={}, transactionId={}",
                    savedPayment.getOrderId(), savedPayment.getTransactionId());

            return mapToResponse(savedPayment);

        } catch (DataIntegrityViolationException ex) {

            // SECOND-LEVEL PROTECTION (race condition)
            log.warn("Duplicate payment detected at DB level (race condition) | orderId={}",
                    request.getOrderId());

            Payment existing = paymentRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment exists but not found"));

            return mapToResponse(existing);
        }
    }

    @Override
    public ResponsePayment getPaymentByOrderId(String orderId) {
        log.info("Fetching payment for orderId={}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for orderId=" + orderId));

        return mapToResponse(payment);
    }

    @Override
    public ResponsePayment updatePaymentStatus(Long paymentId, PaymentStatusUpdateRequest request) {

        log.info("Updating payment status | paymentId={}, newStatus={}",
                paymentId, request.getPaymentStatus());

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        // IDEMPOTENCY CHECK (VERY IMPORTANT)
        if (payment.getPaymentStatus() == request.getPaymentStatus()) {
            log.warn("Duplicate status update ignored | orderId={}, status={}",
                    payment.getOrderId(), payment.getPaymentStatus());
            return mapToResponse(payment);
        }

        // Prevent invalid transitions 
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.warn("Payment already SUCCESS, cannot update again | orderId={}", payment.getOrderId());
            return mapToResponse(payment);
        }

        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
            log.warn("Payment already FAILED, cannot update again | orderId={}", payment.getOrderId());
            return mapToResponse(payment);
        }

        // Update status
        payment.setPaymentStatus(request.getPaymentStatus());
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment status updated | orderId={}, newStatus={}",
                updatedPayment.getOrderId(), updatedPayment.getPaymentStatus());

        // SEND EVENT ONLY ONCE
        if (request.getPaymentStatus() == PaymentStatus.SUCCESS) {

            PaymentCompletedEvent eventMessage = new PaymentCompletedEvent(
                    UUID.randomUUID().toString(),
                    updatedPayment.getOrderId(),
                    updatedPayment.getId().toString(),
                    "SUCCESS",
                    String.valueOf(updatedPayment.getUserId()),
                    LocalDateTime.now()
            );

            log.info("Sending PAYMENT_SUCCESS event | orderId={}", updatedPayment.getOrderId());
            paymentEventProducer.sendPaymentSuccess(eventMessage);

        } else if (request.getPaymentStatus() == PaymentStatus.FAILED) {

            PaymentFailedEvent eventMessage = new PaymentFailedEvent(
                    UUID.randomUUID().toString(),
                    updatedPayment.getOrderId(),
                    updatedPayment.getId().toString(),
                    "FAILED",
                    String.valueOf(updatedPayment.getUserId()),
                    LocalDateTime.now()
            );

            log.info("Sending PAYMENT_FAILED event | orderId={}", updatedPayment.getOrderId());
            paymentEventProducer.sendPaymentFailed(eventMessage);
        }

        return mapToResponse(updatedPayment);
    }

    private ResponsePayment mapToResponse(Payment payment) {
        return new ResponsePayment(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getTransactionId()
        );
    }
}