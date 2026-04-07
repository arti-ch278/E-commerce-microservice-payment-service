package com.artichourey.ecommerce.paymentservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.artichourey.ecommerce.paymentservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.paymentservice.repository.PaymentRepository;
import com.razorpay.Utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @Transactional
    public void processWebhook(String signature, String payload) {
        log.info("[Webhook] Received payload: {}", payload);
        try {
            verifySignature(signature, payload);

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            log.info("[Webhook] Event type: {}", eventType);

            switch (eventType) {
                case "payment.captured" -> handlePaymentSuccess(event);
                case "payment.failed" -> handlePaymentFailed(event);
                default -> log.warn("[Webhook] Unhandled event type: {}", eventType);
            }

        } catch (SecurityException e) {
            log.error("[Webhook] Signature verification failed", e);
            throw e;
        } catch (Exception e) {
            log.error("[Webhook] Error processing Razorpay webhook", e);
            throw e;
        }
    }

    private void handlePaymentSuccess(JSONObject event) {
        JSONObject paymentEntity = extractPaymentEntity(event);
        String razorpayOrderId = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");

        log.info("[Webhook][SUCCESS] Processing payment | orderId={}, paymentId={}", 
                  razorpayOrderId, razorpayPaymentId);

        Payment payment = paymentRepository.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "[Webhook][SUCCESS] Payment not found for orderId: " + razorpayOrderId));

        // Idempotency check
        if (razorpayPaymentId.equals(payment.getGatewayPaymentId()) &&
            payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("[Webhook][SUCCESS] Duplicate webhook received. Ignoring. PaymentId={}", 
                     razorpayPaymentId);
            return; // skip processing
        }

        // Update payment status and save paymentId
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setGatewayPaymentId(razorpayPaymentId);

        log.info("[Webhook][SUCCESS] Payment status updated to SUCCESS for orderId={}", 
                  razorpayOrderId);

        // Send event
        PaymentCompletedEvent eventMessage = new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                payment.getOrderId(),
                payment.getId().toString(),
                payment.getPaymentStatus().toString(),
                String.valueOf(payment.getUserId()),                         
                LocalDateTime.now()                
        );

        paymentEventProducer.sendPaymentSuccess(eventMessage);

        log.info("[Webhook][SUCCESS] PaymentCompletedEvent sent | orderId={}, paymentId={}", 
                  razorpayOrderId, razorpayPaymentId);
    }

    private void handlePaymentFailed(JSONObject event) {
        JSONObject paymentEntity = extractPaymentEntity(event);
        String razorpayOrderId = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");

        log.warn("[Webhook][FAILED] Processing payment | orderId={}, paymentId={}", 
                  razorpayOrderId, razorpayPaymentId);

        Payment payment = paymentRepository.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "[Webhook][FAILED] Payment not found for orderId: " + razorpayOrderId));

        // Idempotency check
        if (razorpayPaymentId.equals(payment.getGatewayPaymentId()) &&
            payment.getPaymentStatus() == PaymentStatus.FAILED) {
            log.warn("[Webhook][FAILED] Duplicate failed webhook received. Ignoring. PaymentId={}", 
                     razorpayPaymentId);
            return;
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setGatewayPaymentId(razorpayPaymentId);

        log.warn("[Webhook][FAILED] Payment status updated to FAILED for orderId={}", 
                  razorpayOrderId);

        // Send failed event
        PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                payment.getOrderId(),
                payment.getId().toString(),
                payment.getPaymentStatus().toString(),
                String.valueOf(payment.getUserId()),                            
                LocalDateTime.now()                
        );

        paymentEventProducer.sendPaymentFailed(failedEvent);

        log.warn("[Webhook][FAILED] PaymentFailedEvent sent | orderId={}, paymentId={}", 
                  razorpayOrderId, razorpayPaymentId);
    }

    private void verifySignature(String signature, String payload) {
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            log.info("[Webhook] Signature verified successfully");
        } catch (Exception e) {
            log.error("[Webhook] Invalid Razorpay signature", e);
            throw new SecurityException("Invalid Razorpay webhook signature");
        }
    }

    private JSONObject extractPaymentEntity(JSONObject event) {
        try {
            JSONObject entity = event.getJSONObject("payload")
                                     .getJSONObject("payment")
                                     .getJSONObject("entity");

            // Check if entity is null or has no keys
            if (entity == null || entity.length() == 0) {
                log.error("[Webhook] Payment entity not found in payload");
                throw new PaymentNotFoundException("Payment entity not found in webhook payload");
            }

            return entity;
        } catch (Exception e) {
            log.error("[Webhook] Invalid webhook payload structure", e);
            throw new PaymentNotFoundException(
                    "Invalid webhook payload or payment entity missing");
        }
    }
    }
