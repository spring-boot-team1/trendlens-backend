package com.test.trend.domain.payment.log.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorLogDTO {

	private Long seqLog;
    private Long seqAccount;
    private String eventType;

    private String eventDetail;
    private LocalDateTime eventTime;
}
