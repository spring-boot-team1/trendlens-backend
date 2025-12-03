package com.test.trend.domain.payment.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqPaymentGenerator")
	@SequenceGenerator(name = "seqPaymentGenerator", sequenceName = "seqPayment", allocationSize = 1)
	private Long seqPayment;
	
	private Long seqAccount;
	private Long seqUserSub;
	
	private Integer amount;
	private String paymentMethod;
	private String paymentStatus; // REQUESTED / APPROVED / CANCELED / FAILED
	
	private LocalDateTime requestTime;
	private LocalDateTime approveTime;
	private LocalDateTime cancelTime;
	
	private String failReason;
	
	// 도메인 메서드
	public void approve() {
		this.paymentStatus = "APPROVED";
		this.approveTime = LocalDateTime.now();
	}
	
	public void cancel() {
		this.paymentStatus = "CANCELED";
		this.cancelTime = LocalDateTime.now();
	}
	
	public void fail(String reason) {
		this.paymentStatus = "FAILED";
		this.failReason = reason;
	}
	
}



