package com.test.trend.domain.payment.subscription.dto;

import java.time.LocalDateTime;

import com.test.trend.enums.SubscriptionStatus;
import com.test.trend.enums.YesNo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

	private Long seqUserSub;
	private Long seqAccount;
	private Long seqSubscriptionPlan;
	private String planName;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime nextBillingDate;
	
	@Enumerated(EnumType.STRING)
	private YesNo autoRenewYn;
	
	@Enumerated(EnumType.STRING)
	private SubscriptionStatus status;
	
	private String cancelReason;
	private LocalDateTime createdAt;
}
