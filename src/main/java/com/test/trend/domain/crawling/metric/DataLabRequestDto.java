package com.test.trend.domain.crawling.metric;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DataLabRequestDto {
    private String startDate;
    private String endDate;
    private String timeUnit;
    private List<KeywordGroup> keywordGroups;

    @Getter
    @Builder
    public static class KeywordGroup {
        private String groupName;
        private List<String> keywords; //[숏패딩, Short padding]
    }
}
