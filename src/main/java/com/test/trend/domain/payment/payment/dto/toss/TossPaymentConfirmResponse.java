package com.test.trend.domain.payment.payment.dto.toss;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossPaymentConfirmResponse {

	private final String paymentKey;
    private final String orderId;
    private final String status;
    private final String requestedAt;
    private final String approvedAt;
    private final String method;
}
