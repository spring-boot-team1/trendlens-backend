package com.test.trend.domain.payment.payment.entity;

import java.time.LocalDateTime;

import com.test.trend.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	private Long amount;
	private String paymentMethod;
	
	@Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // DONE, FAILED, CANCELED, APPROVED 등
	private LocalDateTime requestTime;
	private LocalDateTime approveTime;
	private LocalDateTime cancelTime;
	
	private String failReason;
	
	// Toss Payments 관련 필드 추가
    private String paymentKey; // Toss 고유 결제 키
    private String orderId; // 주문 고유 아이디
	
	// 도메인 메서드
	public void approve() {
		this.paymentStatus = paymentStatus.APPROVED;
		this.approveTime = LocalDateTime.now();
	}
	
	public void cancel() {
		this.paymentStatus = paymentStatus.CANCELED;
		this.cancelTime = LocalDateTime.now();
	}
	
	public void fail(String reason) {
		this.paymentStatus = paymentStatus.FAILED;
		this.failReason = reason;
	}

	public void updateStatus(PaymentStatus status) {
		this.paymentStatus = status;
	}

}

