package com.test.trend.domain.analyze.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyAnalysisRequestDTO {

    private String seqAccount;
    private String imageUrl;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String gender;

}
