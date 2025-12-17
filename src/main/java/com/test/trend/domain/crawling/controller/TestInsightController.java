package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.insight.WeeklyInsightService;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
public class TestInsightController {

    private final WeeklyInsightService weeklyInsightService;
    private final KeywordRepository keywordRepository;

    // 호출 주소 예시: http://localhost:8080/test/insight?keyword=나이키
    @GetMapping("/test/insight")
    public String testInsight(@RequestParam String keyword) {
        Keyword k = keywordRepository.findByKeyword(keyword).orElse(null);

        if (k == null) {
            return "존재하지 않는 키워드입니다. 먼저 크롤링을 수행해주세요.";
        }

        try {
            weeklyInsightService.createWeeklyInsight(k);
            return "성공! DB(weekly_insight 테이블)를 확인해보세요.";
        } catch (Exception e) {
            e.printStackTrace();
            return "실패: " + e.getMessage();
        }
    }
}