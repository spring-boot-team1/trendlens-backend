package com.test.trend.domain.analyze.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyAnalysisWithMetricsDTO {

    private Long seqAccount;
    private String imageUrl;
    private String meshUrl;

    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String gender;

    private BigDecimal bmi;
    private BigDecimal shoulderWidthCm;
    private BigDecimal armLengthCm;
    private BigDecimal legLengthCm;
    private BigDecimal torsoLengthCm;


}
