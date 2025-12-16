package com.test.trend.domain.crawling.insight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightResponseDto {
    private Long seqKeyword;
    private String keyword;       // 예: 에어 조던 1 하이
    private String category;      // 예: 신발
    private String imgUrl;
    private String summary;       // AI 요약 (없으면 "분석 데이터 없음")
    private String stylingTip;    // AI 팁 (없으면 null)
    private boolean hasInsight;   // 분석 데이터 존재 여부 (true/false)


}