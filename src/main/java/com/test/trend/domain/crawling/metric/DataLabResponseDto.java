package com.test.trend.domain.crawling.metric;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class DataLabResponseDto {
    private List<Result> results;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Result {
        private String title;
        private List<Item> data;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Item {
        private String period;  //2026-01-01
        private Double ratio;   // 검색량(0~100)
    }
}
