package com.test.trend.domain.analyze.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FashionRecommendDTO {

    private String promptUsed;
    private String aiResult;

}
