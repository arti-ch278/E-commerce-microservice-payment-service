package com.artichourey.ecommerce.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.paymentservice.repository.PaymentRepository;
import com.artichourey.ecommerce.paymentservice.serviceImpl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @Test
    void createPayment_NewPayment_ShouldSaveAndReturn() {
        RequestPayment request = RequestPayment.builder()
                .orderId("ORD123")
                .amount(BigDecimal.valueOf(500))
                .paymentMethod(PaymentMethod.UPI)
                .userId(1L)
                .build();

        when(paymentRepository.findByOrderId("ORD123")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponsePayment response = paymentService.createPayment(request);

        assertEquals("ORD123", response.getOrderId());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayment_ExistingPayment_ShouldReturnExisting() {
        Payment existing = Payment.builder()
                .id(1L)
                .orderId("ORD123")
                .amount(BigDecimal.valueOf(500))
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN123")
                .build();

        when(paymentRepository.findByOrderId("ORD123")).thenReturn(Optional.of(existing));

        ResponsePayment response = paymentService.createPayment(
                RequestPayment.builder().orderId("ORD123").amount(BigDecimal.valueOf(500)).build());

        assertEquals(existing.getTransactionId(), response.getTransactionId());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void updatePaymentStatus_Success_ShouldSendEvent() {
        Payment payment = Payment.builder()
                .id(1L)
                .orderId("ORD123")
                .amount(BigDecimal.valueOf(500))
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId("TXN123")
                .userId(1L)
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(PaymentStatus.SUCCESS);
        ResponsePayment response = paymentService.updatePaymentStatus(1L, request);

        assertEquals(PaymentStatus.SUCCESS, response.getPaymentStatus());
        verify(paymentEventProducer).sendPaymentSuccess(any(PaymentCompletedEvent.class));
    }
}
