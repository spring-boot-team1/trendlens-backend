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
public class UserKeywordInsightDTO {

	private Long seqUserKeywordInsight;
    private Long seqAccount;
    private Long seqKeyword;

    private String insightText;
    private Double trendScore;
    private String hotYn;

    private LocalDateTime createdAt;
}
