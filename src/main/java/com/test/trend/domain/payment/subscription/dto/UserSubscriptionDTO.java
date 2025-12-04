package com.test.trend.domain.payment.subscription.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

	private Long seqUserSub;
	private Long seqAccount;
	private Long seqSubscriptionPlan;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime nextBillingDate;
	
	private String autoRenewYn;
	private String status;
	private String cancelReason;
	
	private LocalDateTime createdAt;
}
