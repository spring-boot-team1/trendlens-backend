package com.test.trend.domain.analyze.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyAnalysisWithMetricsDTO {

    private Long seqAccount;
    @Setter
    private String imageUrl;
    @Setter
    private String meshUrl;

    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String gender;

    private BigDecimal bmi;
    private BigDecimal shoulderWidthCm;
    private BigDecimal armLengthCm;
    private BigDecimal legLengthCm;
    private BigDecimal torsoLengthCm;

    @Setter
    private Long seqBodyAnalysis;     // BodyAnalysis PK
    @Setter
    private Long seqBodyMetrics;      // BodyMetrics PK
    @Setter
    private String promptUsed;
    @Setter
    private String aiResult;

}
