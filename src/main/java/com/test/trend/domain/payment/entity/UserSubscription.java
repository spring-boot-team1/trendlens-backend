package com.test.trend.domain.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSubscription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserSubGenerator")
	@SequenceGenerator(name = "seqUserSubGenerator", sequenceName = "seqUserSub", allocationSize = 1)
	private Long seqUserSub;
	
	// FK
	private Long seqAccount;
	private Long seqSubscriptionPlan;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime nextBillingDate;
	
	private String autoRenewYn;	// Y/N
	private String status;	// ACTIVE/CANCELED/EXPIRED
	
	private String cancelReason;
	
	private LocalDateTime createdAt;
	
	// 도메인 메서드
	public void cancel(String reason) {
		this.status = "CANCLED";
		this.cancelReason = reason;
	}
	
	public void renew(LocalDateTime nextDate) {
		this.nextBillingDate = nextDate;
	}

}
