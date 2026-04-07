package com.artichourey.ecommerce.paymentservice.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.artichourey.ecommerce.paymentservice.entity.PaymentOrderInfo;


public interface PaymentOrderInfoRepository extends JpaRepository<PaymentOrderInfo, String> {

	 @Query("SELECT p.amount FROM PaymentOrderInfo p WHERE p.orderId = :orderId")
	    BigDecimal findAmountByOrderId(@Param("orderId") String orderId);


}
