package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.service.TrendPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlingController {

    private final TrendPipelineService trendPipelineService;

    @PostMapping("/run")
    public ResponseEntity<String> startCrawling() {
        try {
            trendPipelineService.runCrawlingFlow();

            return ResponseEntity.ok("크롤링 및 데이터 분석 파이프 라인이 성공적으로 실행되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("실행 중 오류 발생:" + e.getMessage());
        }
    }


}
