package com.artichourey.ecommerce.paymentservice.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private String eventId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private LocalDateTime eventTime;
}
