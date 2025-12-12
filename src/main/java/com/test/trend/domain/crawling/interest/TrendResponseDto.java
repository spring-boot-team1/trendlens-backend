package com.test.trend.domain.crawling.interest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendResponseDto {
    private Long seqKeyword;
    private String keyword;
    private String category;
    private String trendDate;

    //AI 분석 데이터
    private String aiSummary;
    private String aiStylingTip;

    //시각자료
    private String thumbnail;

    //트렌드 점수(랭킹)
    private int trendScore;
}
