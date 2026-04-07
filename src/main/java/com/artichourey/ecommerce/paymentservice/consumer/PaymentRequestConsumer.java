package com.artichourey.ecommerce.paymentservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.artichourey.ecommerce.events.PaymentRequestEvent;
import com.artichourey.ecommerce.paymentservice.entity.PaymentOrderInfo;
import com.artichourey.ecommerce.paymentservice.repository.PaymentOrderInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestConsumer {

    private final PaymentOrderInfoRepository paymentOrderInfoRepository;

    @KafkaListener(
    	    topics = "payment-request-topic",
    	    groupId = "payment-service-group",
    	    containerFactory = "kafkaListenerContainerFactory"
    	)
    	public void handlePaymentRequest(PaymentRequestEvent event) {
    	    log.info("Received PaymentRequestEvent | orderId={}", event.getOrderId());

    	    try {
    	        if (!paymentOrderInfoRepository.existsById(event.getOrderId())) {
    	            PaymentOrderInfo info = new PaymentOrderInfo(
    	                    event.getOrderId(),
    	                    Long.parseLong(event.getUserId()),
    	                    event.getAmount()
    	            );
    	            paymentOrderInfoRepository.save(info);
    	        }

    	    } catch (Exception ex) {
    	        log.error("Error processing event, sending to retry | orderId={}", event.getOrderId());
    	        throw ex; // important for retry
    	    }
    	}
}