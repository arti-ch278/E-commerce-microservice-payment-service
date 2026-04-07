package com.artichourey.ecommerce.paymentservice.dto;

import java.math.BigDecimal;

import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for creating a payment")
public class RequestPayment {
	
	@NotNull
	@Schema(description = "Order ID associated with payment", example = "ORD12345", required = true)
	private String orderId;
	
	@Positive
	@Schema(description = "Payment amount", example = "499.99", required = true)
	private BigDecimal amount;
	
	@NotNull
	@Schema(
		    description = "Payment method used (CARD, UPI, NET_BANKING, WALLET, CASH_ON_DELIVERY)",
		    example = "UPI",
		    required = true)
	private PaymentMethod paymentMethod;
	private Long userId;

}
