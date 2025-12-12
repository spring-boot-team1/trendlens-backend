package com.test.trend.domain.payment.subscription.entity;

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
public class SubscriptionPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqSubscriptionPlanGenerator")
    @SequenceGenerator(name = "seqSubscriptionPlanGenerator", sequenceName = "seqSubscriptionPlan", allocationSize = 1)
    private Long seqSubscriptionPlan;
	
	private String planName;
	private String planDescription;
	private Long monthlyFee;          // NUMBER(10,0)
    private Integer durationMonth;
	
	private String status; //ACTIVE/INACTIVE
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	/** 플랜 상태 변경 */
	public void updateStatus(String newStatus) {
		this.status = newStatus;
		this.updatedAt = LocalDateTime.now();
	}
	
}



