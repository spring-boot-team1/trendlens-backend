package com.test.trend.domain.payment.trend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTrendHistoryDTO {

	private Long seqUserTrendHistory;
    private Long seqAccount;
    private Long seqKeyword;

    private LocalDateTime viewAt;
    private String sourcePage;
	
}
