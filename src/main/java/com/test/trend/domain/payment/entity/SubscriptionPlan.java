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
public class SubscriptionPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqSubscriptionPlanGenerator")
    @SequenceGenerator(name = "seqSubscriptionPlanGenerator", sequenceName = "seqSubscriptionPlan", allocationSize = 1)
    private Long seqSubscriptionPlan;
	
	// FK
	private Long seqAccount;
	
	private String planName;
	private String planDescription;
	
	private Integer monthlyFee;
	private Integer durationMonth;
	
	private String status; //ACTIVE/INACTIVE
	
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	
	// 상태 변경 메서드
	public void updateStatus(String newStatus) {
		this.status = newStatus;
		this.updateAt = LocalDateTime.now();
	}
	
}



