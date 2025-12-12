package com.test.trend.domain.payment.subscription.dto;

import java.time.LocalDateTime;

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
public class SubscriptionPlanDTO {
	
	private Long seqSubscriptionPlan;

	private String planName;
    private String planDescription;

    private Long monthlyFee;
    private Integer durationMonth;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
