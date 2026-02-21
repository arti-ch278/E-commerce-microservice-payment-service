package com.artichourey.ecommerce.paymentservice.dto;

import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusUpdateRequest {
	@NotNull
	private PaymentStatus paymentStatus;

}
