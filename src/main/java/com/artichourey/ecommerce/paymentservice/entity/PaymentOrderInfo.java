package com.artichourey.ecommerce.paymentservice.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_order_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderInfo {
	
    @Id
    private String orderId;

    private Long userId;

    private BigDecimal amount;
}
