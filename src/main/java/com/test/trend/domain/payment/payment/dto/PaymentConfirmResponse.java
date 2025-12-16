package com.test.trend.domain.payment.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentConfirmResponse {

    private String orderId;
    private Long amount;
    private String status;
    private LocalDateTime nextBillingDate;
}
