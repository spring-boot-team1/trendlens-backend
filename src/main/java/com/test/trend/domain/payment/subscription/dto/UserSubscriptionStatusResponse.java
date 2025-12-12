package com.test.trend.domain.payment.subscription.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSubscriptionStatusResponse {

    private String planName;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime nextBillingDate;
}
