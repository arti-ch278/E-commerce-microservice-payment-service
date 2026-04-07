package com.artichourey.ecommerce.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaDLQConfig {

	@Bean
	public NewTopic paymentRequestDLQ() {
	    return TopicBuilder.name("payment-request-topic.DLT")
	            .partitions(3)
	            .replicas(1)
	            .build();
	}
}
