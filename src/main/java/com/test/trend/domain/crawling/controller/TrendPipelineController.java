package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.service.TrendPipelineService;
import com.test.trend.domain.crawling.score.TrendScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TrendPipelineController {

    private final TrendPipelineService trendPipelineService;
    private final TrendScoreService trendScoreService;

    // 1. 전체 파이프라인 실행 (크롤링 -> 분석 -> 점수계산)
    // 브라우저에서 접속: http://localhost:8080/api/test/run
    @GetMapping("/run")
    public ResponseEntity<String> runFullPipeline() {
        System.out.println("========= [Manual Trigger] 전체 파이프라인 시작 =========");

        try {
            trendPipelineService.runCrawlingFlow();
            return ResponseEntity.ok("✅ 전체 파이프라인 실행 완료! (콘솔 로그를 확인하세요)");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ 실행 중 오류 발생: " + e.getMessage());
        }
    }

    // 2. 점수 계산만 다시 돌리기 (크롤링 없이 로직 검증용)
    // 브라우저에서 접속: http://localhost:8080/api/test/recalc
    @GetMapping("/recalc")
    public ResponseEntity<String> runScoreRecalc() {
        System.out.println("========= [Manual Trigger] 점수 재계산 시작 =========");

        try {
            trendScoreService.recalcTodayScores();
            return ResponseEntity.ok("✅ 점수/랭킹 재계산 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 계산 중 오류: " + e.getMessage());
        }
    }
}
