package com.artichourey.ecommerce.paymentservice.serviceImpl;

import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.exception.PaymentNotFoundException;
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
public class PaymentServiceImpl implements PaymentService{
	
	private final PaymentRepository paymentRepository;

	@Override
	public ResponsePayment createPayment(RequestPayment request) {
		log.info("Creating new payment entity for orderId: {}, amount: {}", request.getOrderId(), request.getAmount());
		Payment payment =Payment.builder().orderId(request.getOrderId())
		.amount(request.getAmount())
		.paymentMethod(request.getPaymentMethod())
		.paymentStatus(PaymentStatus.PENDING)
		.transactionId(TransactionGenerate.generate()).build();
		Payment savedPayment=paymentRepository.save(payment);
		 log.info("Payment saved with transactionId: {}, status: {}", savedPayment.getTransactionId(), savedPayment.getPaymentStatus());
		
		return mapToResponse(savedPayment);
	}

	@Override
	public ResponsePayment getPaymentByOrderId(String orderId) {
		log.info("Fetching payment for orderId: {}", orderId);
		Payment payment= paymentRepository.findByOrderId(orderId)
				.orElseThrow(()-> new PaymentNotFoundException("payment not found exception"+orderId));
		 log.info("Payment retrieved: transactionId: {}, status: {}", payment.getTransactionId(), payment.getPaymentStatus());
		return mapToResponse(payment);
	}

	@Override
	public ResponsePayment updatePaymentStatus(Long paymentId, PaymentStatusUpdateRequest paymentStatusUpdateRequest) {
		log.info("Updating payment status for paymentId: {} to {}", paymentId, paymentStatusUpdateRequest.getPaymentStatus());
		Payment payment =paymentRepository.findById(paymentId).
		orElseThrow(()-> new PaymentNotFoundException("payment not found exception"+paymentId));
		payment.setPaymentStatus(paymentStatusUpdateRequest.getPaymentStatus());
		log.info("Payment status updated: transactionId: {}, new status: {}", payment.getTransactionId(), payment.getPaymentStatus());
		return mapToResponse(payment);
	}
	public ResponsePayment mapToResponse(Payment payment) {
		
		return new ResponsePayment(payment.getId(),payment.getOrderId(),payment.getAmount()
				,payment.getPaymentStatus(),payment.getTransactionId());
		
	}

}
