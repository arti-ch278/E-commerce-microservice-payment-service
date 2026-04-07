package com.artichourey.ecommerce.paymentservice.dto;

import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body to update payment status")
public class PaymentStatusUpdateRequest {
	
	@NotNull
	@Schema(
		    description = "New payment status. Possible values: PENDING, SUCCESS, FAILED, CANCELLED",
		    example = "SUCCESS",
		    required = true)
	private PaymentStatus paymentStatus;

}
