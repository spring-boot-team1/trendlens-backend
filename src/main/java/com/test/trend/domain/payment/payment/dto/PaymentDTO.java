package com.test.trend.domain.payment.payment.dto;

import java.time.LocalDateTime;

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

    private Integer amount;
    private String paymentMethod;
    private String paymentStatus;

    private LocalDateTime requestTime;
    private LocalDateTime approveTime;
    private LocalDateTime cancelTime;

    private String failReason;
}
