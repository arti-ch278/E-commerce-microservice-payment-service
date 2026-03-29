package com.artichourey.ecommerce.paymentservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_SUCCESS_TOPIC = "payment-success-topic";
    private static final String PAYMENT_FAILED_TOPIC = "payment-failed-topic";

    public void sendPaymentSuccess(PaymentCompletedEvent event) {
        log.info("Sending PaymentCompletedEvent | orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId(), event.getUserId());

        kafkaTemplate.send(PAYMENT_SUCCESS_TOPIC, event.getOrderId(), event);

        log.info("PaymentCompletedEvent sent successfully | orderId={}", event.getOrderId());
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        log.warn("Sending PaymentFailedEvent | orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId(),event.getUserId());

        kafkaTemplate.send(PAYMENT_FAILED_TOPIC, event.getOrderId(), event);

        log.info("PaymentFailedEvent sent successfully | orderId={}", event.getOrderId());
    }
}