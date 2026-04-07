package com.artichourey.ecommerce.paymentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.artichourey.ecommerce.paymentservice.serviceImpl.RazorpayWebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment Webhooks", description = "Endpoints for payment provider webhooks")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class WebhookController {

    private final RazorpayWebhookService razorpayWebhookService;

    @Operation(
        summary = "Handle Razorpay webhook",
        description = "Receives webhook events from Razorpay. Public endpoint, no JWT required."
    )
    @PostMapping("/webhook/razorpay")
    public ResponseEntity<Void> handleWebhook( 
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) {

        razorpayWebhookService.processWebhook(signature, payload);

        return ResponseEntity.noContent().build(); 
    }
}
