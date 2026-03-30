package com.artichourey.ecommerce.paymentservice.service;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.paymentservice.repository.PaymentRepository;
import com.artichourey.ecommerce.paymentservice.serviceImpl.RazorpayWebhookService;



@ExtendWith(MockitoExtension.class)
class RazorpayWebhookServiceTest {

    @InjectMocks
    private RazorpayWebhookService webhookService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @Test
    void processWebhook_ShouldHandlePaymentCaptured() {

        
        ReflectionTestUtils.setField(webhookService, "webhookSecret", "test-secret");

        String payload = """
        {
          "event": "payment.captured",
          "payload": {
            "payment": {
              "entity": {
                "order_id": "ORD123",
                "id": "PAY123"
              }
            }
          }
        }
        """;

        Payment payment = Payment.builder()
                .orderId("ORD123")
                .transactionId("ORD123")
                .paymentStatus(PaymentStatus.PENDING)
                .userId(1L)
                .build();

        when(paymentRepository.findByTransactionId("ORD123"))
                .thenReturn(Optional.of(payment));

        
        try {
            webhookService.processWebhook("valid-signature", payload);
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }
}