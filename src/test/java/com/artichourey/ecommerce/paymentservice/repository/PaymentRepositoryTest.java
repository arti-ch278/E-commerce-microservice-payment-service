package com.artichourey.ecommerce.paymentservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setup() {
        payment = Payment.builder()
                .orderId("ORD123")
                .amount(BigDecimal.valueOf(499.99))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.UPI)
                .transactionId("TXN123")
                .userId(1L)
                .build();
        paymentRepository.save(payment);
    }

    @Test
    void findByOrderId_ShouldReturnPayment() {
        Optional<Payment> found = paymentRepository.findByOrderId("ORD123");
        assertTrue(found.isPresent());
        assertEquals("ORD123", found.get().getOrderId());
        assertEquals("TXN123", found.get().getTransactionId());
    }

    @Test
    void findByTransactionId_ShouldReturnPayment() {
        Optional<Payment> found = paymentRepository.findByTransactionId("TXN123");
        assertTrue(found.isPresent());
        assertEquals(PaymentStatus.PENDING, found.get().getPaymentStatus());
    }

    @Test
    void findByOrderId_NonExisting_ShouldReturnEmpty() {
        Optional<Payment> found = paymentRepository.findByOrderId("NON_EXISTENT");
        assertTrue(found.isEmpty());
    }

    @Test
    void save_ShouldPersistPayment() {
        Payment newPayment = Payment.builder()
                .orderId("ORD999")
                .amount(BigDecimal.valueOf(1000))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .transactionId("TXN999")
                .userId(2L)
                .build();

        Payment saved = paymentRepository.save(newPayment);
        assertNotNull(saved.getId());
        assertEquals("ORD999", saved.getOrderId());
    }
}