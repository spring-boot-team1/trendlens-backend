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
public class TrendSnapshotDTO {

	private Long seqTrendSnapshot;
    private LocalDateTime snapshotDate;

    private String topBrand;
    private String topCategory;
    private String styleTrend;

    private LocalDateTime createdAt;
}
