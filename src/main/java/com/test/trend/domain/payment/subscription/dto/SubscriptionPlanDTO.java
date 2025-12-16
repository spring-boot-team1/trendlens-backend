package com.test.trend.domain.payment.subscription.dto;

import java.time.LocalDateTime;

import com.test.trend.enums.SubscriptionStatus;

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
public class SubscriptionPlanDTO {
	
	private Long seqSubscriptionPlan;

	private String planName;
    private String planDescription;

    private Long monthlyFee;
    private Integer durationMonth;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
