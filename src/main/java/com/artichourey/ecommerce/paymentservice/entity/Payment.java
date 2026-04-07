package com.artichourey.ecommerce.paymentservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.artichourey.ecommerce.paymentservice.enums.PaymentMethod;
import com.artichourey.ecommerce.paymentservice.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="payments", indexes= {
		@Index(columnList="orderId"),
		@Index(columnList="transactionId")
})
public class Payment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false, unique=true)
	private String orderId;
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(50)")
	//@Column(nullable=false)
	private PaymentStatus paymentStatus;
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(50)")
	//@Column(nullable=false)
	private PaymentMethod paymentMethod;
	@Column(unique=true,nullable=true)
	private String transactionId;
	private String gatewayPaymentId;
	private Long userId;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@PrePersist
	void createdOn() {
		this.createdAt=LocalDateTime.now();
		this.updatedAt=LocalDateTime.now();	
	}
	@PreUpdate
	void updatedOn() {
		this.updatedAt=LocalDateTime.now();
	}

}
