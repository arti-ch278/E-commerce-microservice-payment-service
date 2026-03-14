package com.artichourey.ecommerce.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment APIs", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	@Operation(summary = "Create a payment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment created successfully",
            content = @Content(schema = @Schema(implementation = ResponsePayment.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
	@PostMapping("/")
	public ResponseEntity<ResponsePayment> createPayment(@Valid @RequestBody RequestPayment request){
		ResponsePayment payment=paymentService.createPayment(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(payment);
		
	}
	@Operation(summary = "Get payment by order ID", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/order/{orderId}")
	public ResponseEntity<ResponsePayment> getByOrderID(@PathVariable String orderId){
		
		ResponsePayment response=paymentService.getPaymentByOrderId(orderId);
		
		return ResponseEntity.ok(response);
		
	}
	@Operation(summary = "Update payment status", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/{paymentId}/status")
	public ResponseEntity<ResponsePayment> updateStatus(@PathVariable Long paymentId, @Valid @RequestBody PaymentStatusUpdateRequest paymentStatusUpdateRequest){
		
		ResponsePayment response=paymentService.updatePaymentStatus(paymentId, paymentStatusUpdateRequest);
		return ResponseEntity.ok(response);
		
	}
	
}
