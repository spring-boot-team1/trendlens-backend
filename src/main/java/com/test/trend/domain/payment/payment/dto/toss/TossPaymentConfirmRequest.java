package com.test.trend.domain.payment.payment.dto.toss;

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
public class TossPaymentConfirmRequest {

	private String paymentKey;
    private String orderId;
    private Long amount;
    
    // 구독 갱신에 반드시 필요
    private Long seqAccount;
    
}
