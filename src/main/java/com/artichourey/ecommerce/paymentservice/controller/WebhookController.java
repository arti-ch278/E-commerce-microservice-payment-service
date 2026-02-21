package com.artichourey.ecommerce.paymentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.artichourey.ecommerce.paymentservice.serviceImpl.RazorpayWebhookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class WebhookController {
	
	private final RazorpayWebhookService razorpayWebhookService;	
	
	@PostMapping("/webhook/razorpay")
	public ResponseEntity<Void> handelWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload){
		razorpayWebhookService.processWebhook(signature, payload);
		return ResponseEntity.ok().build();
		}

}
