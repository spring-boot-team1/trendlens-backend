package com.test.trend.domain.payment.payment.dto.toss;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentConfirmResponse {

	private String paymentKey;
    private String orderId;
    private String status;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Long totalAmount;
    private String method;

    private CardInfo card;
    private ReceiptInfo receipt;
    
    // --- 내부 중첩 객체 DTO ---
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        private String number;              // 마스킹 카드번호
        private String approveNo;           // 승인번호
        private Integer installmentPlanMonths;
        private Boolean isInterestFree;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptInfo {
        private String url; // 영수증 URL
    }
}
