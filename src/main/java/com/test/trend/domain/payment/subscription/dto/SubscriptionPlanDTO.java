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
public class SubscriptionPlanDTO {
	
	private Long seqSubscriptionPlan;
	private Long seqAccount;

    private String planName;
    private String planDescription;

    private Integer monthlyFee;
    private Integer durationMonth;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
