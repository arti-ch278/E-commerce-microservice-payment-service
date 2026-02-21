package com.artichourey.ecommerce.paymentservice.serviceImpl;

import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.paymentservice.dto.PaymentStatusUpdateRequest;
import com.artichourey.ecommerce.paymentservice.dto.RequestPayment;
import com.artichourey.ecommerce.paymentservice.dto.ResponsePayment;
import com.artichourey.ecommerce.paymentservice.entity.Payment;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;
import com.artichourey.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.artichourey.ecommerce.paymentservice.repository.PaymentRepository;
import com.artichourey.ecommerce.paymentservice.service.PaymentService;
import com.artichourey.ecommerce.paymentservice.util.TransactionGenerate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService{
	
	private final PaymentRepository paymentRepository;

	@Override
	public ResponsePayment createPayment(RequestPayment request) {
		Payment payment =Payment.builder().orderId(request.getOrderId())
		.amount(request.getAmount())
		.paymentMethod(request.getPaymentMethod())
		.paymentStatus(PaymentStatus.PENDING)
		.transactionId(TransactionGenerate.generate()).build();
		Payment savedPayment=paymentRepository.save(payment);
		
		
		return mapToResponse(savedPayment);
	}

	@Override
	public ResponsePayment getPaymentByOrderId(String orderId) {
		Payment payment= paymentRepository.findByOrderId(orderId)
				.orElseThrow(()-> new PaymentNotFoundException("payment not found exception"+orderId));
		
		return mapToResponse(payment);
	}

	@Override
	public ResponsePayment updatePaymentStatus(Long paymentId, PaymentStatusUpdateRequest paymentStatusUpdateRequest) {
		Payment payment =paymentRepository.findById(paymentId).
		orElseThrow(()-> new PaymentNotFoundException("payment not found exception"+paymentId));
		payment.setPaymentStatus(paymentStatusUpdateRequest.getPaymentStatus());
		
		return mapToResponse(payment);
	}
	public ResponsePayment mapToResponse(Payment payment) {
		
		return new ResponsePayment(payment.getId(),payment.getOrderId(),payment.getAmount()
				,payment.getPaymentStatus(),payment.getTransactionId());
		
	}

}
