package com.artichourey.ecommerce.paymentservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;
    private String orderId;
    private String transactionId;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();

        orderId = "ORD" + UUID.randomUUID();
        transactionId = "TXN" + UUID.randomUUID();

        payment = Payment.builder()
                .orderId(orderId)
                .amount(BigDecimal.valueOf(499.99))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.UPI)
                .transactionId(transactionId)
                .userId(1L)
                .build();

        paymentRepository.saveAndFlush(payment);
    }

    @Test
    void findByOrderId_ShouldReturnPayment() {
        Optional<Payment> found = paymentRepository.findByOrderId(orderId);

        assertTrue(found.isPresent());
        assertEquals(orderId, found.get().getOrderId());
        assertEquals(transactionId, found.get().getTransactionId());
        assertEquals(PaymentStatus.PENDING, found.get().getPaymentStatus());
    }

    @Test
    void findByTransactionId_ShouldReturnPayment() {
        Optional<Payment> found = paymentRepository.findByTransactionId(transactionId);

        assertTrue(found.isPresent());
        assertEquals(orderId, found.get().getOrderId());
        assertEquals(PaymentStatus.PENDING, found.get().getPaymentStatus());
    }

    @Test
    void findByOrderId_NonExisting_ShouldReturnEmpty() {
        Optional<Payment> found = paymentRepository.findByOrderId("NON_EXISTENT");

        assertTrue(found.isEmpty());
    }

    @Test
    void save_ShouldPersistPayment() {
        String newOrderId = "ORD" + UUID.randomUUID();
        String newTxnId = "TXN" + UUID.randomUUID();

        Payment newPayment = Payment.builder()
                .orderId(newOrderId)
                .amount(BigDecimal.valueOf(1000))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .transactionId(newTxnId)
                .userId(2L)
                .build();

        Payment saved = paymentRepository.saveAndFlush(newPayment);

        assertNotNull(saved.getId());
        assertEquals(newOrderId, saved.getOrderId());
        assertEquals(newTxnId, saved.getTransactionId());
    }
}