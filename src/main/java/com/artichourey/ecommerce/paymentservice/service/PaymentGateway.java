package com.artichourey.ecommerce.paymentservice.service;

import java.math.BigDecimal;

import com.artichourey.ecommerce.paymentservice.dto.GatewayOrderResponse;

public interface PaymentGateway {
	
	GatewayOrderResponse createOrder(String order, BigDecimal amount);

}
