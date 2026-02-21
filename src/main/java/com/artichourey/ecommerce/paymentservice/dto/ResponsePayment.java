package com.artichourey.ecommerce.paymentservice.dto;

import java.math.BigDecimal;

import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePayment {
	
	private Long paymentId;
	private String orderId;
	private BigDecimal amount;
	private PaymentStatus paymentStatus;
	private  String transactionId;

}
