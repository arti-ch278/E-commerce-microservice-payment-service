package com.artichourey.ecommerce.paymentservice.serviceImpl;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.paymentservice.dto.GatewayOrderResponse;
import com.artichourey.ecommerce.paymentservice.service.PaymentGateway;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayPaymentGateway implements PaymentGateway {
	
	private final RazorpayClient razorpayClient;

	@Override
	public GatewayOrderResponse createOrder(String orderId, BigDecimal amount) {
		try {
		JSONObject options= new JSONObject();
		options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
		options.put("currency", "INR");
		options.put("receipt", orderId);
		Order order=razorpayClient.orders.create(options);
		return new GatewayOrderResponse(
				order.get("id"),
				order.get("currency"),
				amount,
				order.get("status"),
				"RAZORPAY");
		}catch(Exception e) {
			throw new IllegalStateException("Razorpay Order creation failed");
		}
	}

}
