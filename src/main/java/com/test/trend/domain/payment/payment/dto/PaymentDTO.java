package com.test.trend.domain.payment.payment.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.test.trend.enums.PaymentStatus;

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
public class PaymentDTO {

	private Long seqPayment;
    private Long seqAccount;
    private Long seqUserSub;
    private Long seqSubscriptionPlan;
    
    private String orderId;
    private String paymentKey;

    private Long amount;
    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime requestTime;
    private OffsetDateTime approveTime;
    private LocalDateTime cancelTime;

    private String failReason;
    
    // Toss 영수증 URL
    private String receiptUrl;

    // 카드 승인번호
    private String cardApproveNo;

    // 구독 갱신 결과 (추가)
    private LocalDateTime nextBillingDate;

}
