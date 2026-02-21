package com.artichourey.ecommerce.paymentservice.dto;

import java.math.BigDecimal;

import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RequestPayment {
	
	@NotNull
	private String orderId;
	@Positive
	private BigDecimal amount;
	@NotNull
	private PaymentMethod paymentMethod;

}
