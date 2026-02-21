package com.artichourey.ecommerce.paymentservice.service;

import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;

public interface PaymentService {
	
	ResponsePayment createPayment(RequestPayment request);
	ResponsePayment getPaymentByOrderId(String orderId);
	ResponsePayment updatePaymentStatus(Long paymentId, PaymentStatusUpdateRequest paymentStatusUpdateRequest);
	
	

}
