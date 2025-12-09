package com.test.trend.domain.payment.payment.dto.toss;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossPaymentConfirmRequest {

	private final String paymentKey;
    private final String orderId;
    private final Long amount;
}
