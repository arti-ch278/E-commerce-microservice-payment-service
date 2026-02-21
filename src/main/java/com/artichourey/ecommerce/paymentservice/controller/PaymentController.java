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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	
	@PostMapping("/")
	public ResponseEntity<ResponsePayment> createPayment(@Valid @RequestBody RequestPayment request){
		ResponsePayment payment=paymentService.createPayment(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(payment);
		
	}
	@GetMapping("/order/{orderId}")
	public ResponseEntity<ResponsePayment> getByOrderID(@PathVariable String orderId){
		
		ResponsePayment response=paymentService.getPaymentByOrderId(orderId);
		
		return ResponseEntity.ok(response);
		
	}
	@PutMapping("/{paymentId}/status")
	public ResponseEntity<ResponsePayment> updateStatus(@PathVariable Long paymentId, @Valid @RequestBody PaymentStatusUpdateRequest paymentStatusUpdateRequest){
		
		ResponsePayment response=paymentService.updatePaymentStatus(paymentId, paymentStatusUpdateRequest);
		return ResponseEntity.ok(response);
		
	}
	
}
