package com.test.trend.domain.payment.payment.dto;

import java.time.LocalDateTime;

import com.test.trend.enums.PaymentStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

	private Long seqPayment;
    private Long seqAccount;
    private Long seqUserSub;

    private Long amount;
    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime requestTime;
    private LocalDateTime approveTime;
    private LocalDateTime cancelTime;

    private String failReason;
}
