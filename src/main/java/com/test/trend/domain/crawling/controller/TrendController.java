package com.test.trend.domain.crawling.controller;


import com.test.trend.domain.crawling.interest.TrendResponseDto;
import com.test.trend.domain.crawling.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trend API", description = "트렌드 분석 데이터 조회")
@RestController
@RequestMapping("/api/trends")
public class TrendController {

    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @Operation(summary = "실시간 트렌드 랭킹 조회", description = "TrendScore 기준 상위 10개 키워드를 반환합니다.")
    @GetMapping("/rank")
    public ResponseEntity<List<TrendResponseDto>> getTrendRanking() {
        List<TrendResponseDto> ranking = trendService.getTrendRanking();
        return ResponseEntity.ok(ranking);
    }

    @Operation(summary = "키워드 AI 주간 분석 조회", description = "특정 키워드의 이번 주 트렌드 요약과 스타일링 팁을 반환합니다.")
    @GetMapping("/insight")
    public ResponseEntity<String> getWeeklyInsight(@RequestParam String keyword) {
        String report = trendService.getWeeklyInsight(keyword);
        return ResponseEntity.ok(report);
    }
}
