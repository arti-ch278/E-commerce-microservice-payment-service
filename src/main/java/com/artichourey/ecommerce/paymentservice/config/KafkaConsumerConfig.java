package com.artichourey.ecommerce.paymentservice.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Retry: 3 times with 5 seconds delay
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, 3);

        // Dead Letter publishing recoverer
        DeadLetterPublishingRecoverer recoverer =
        	    new DeadLetterPublishingRecoverer(kafkaTemplate,
        	        (record, ex) -> new TopicPartition("payment-request-topic.DLT", record.partition()));

        // Error handler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Retrying message | topic={} | partition={} | attempt={}",
                    record.topic(), record.partition(), deliveryAttempt);
        });

        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);

        return factory;
    }
}
