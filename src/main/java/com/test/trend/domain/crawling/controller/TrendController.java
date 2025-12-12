package com.test.trend.domain.crawling.controller;


import com.test.trend.domain.crawling.interest.InsightResponseDto;
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

    //1. [공통] 검색 & Insight 조회 (유연한 검색 적용됨)
    @Operation(summary = "키워드 AI 주간 분석 조회", description = "검색어와 유사한 키워드의 이번 주 트렌드 요약/팁을 반환합니다.")
    @GetMapping("/insight")
    public ResponseEntity<List<InsightResponseDto>> getWeeklyInsight(@RequestParam String keyword) {
        List<InsightResponseDto> result = trendService.getWeeklyInsight(keyword);
        return  ResponseEntity.ok(result);
    }

    //2. [비로그인]
    @Operation(summary = "[Guest] 실시간 Top 5 조회", description = "로그인하지 않은 사용자를 위한 전체 Top 5 키워드")
    @GetMapping("/rank/guest")
    public ResponseEntity<List<TrendResponseDto>> getGuest() {
        List<TrendResponseDto> ranking = trendService.getGuestTop5();
        return ResponseEntity.ok(ranking);
    }

    //3. [로그인] 내 관심 키워드 래킹 조회
    @Operation(summary = "[Member] 내 관심 키워드 순위", description = "로그인한 회원이 등록한 관심 키워드의 현재 순위를 보여줍니다.")
    @GetMapping("/rank/my")
    public ResponseEntity<List<TrendResponseDto>> getMyRank() {
        //Long seqAccount = account.getseqAccount();
        //List<TrendResponseDto> myRanking = trendService.getAccountRanks(seqAccount);
        //return ResponseEntity.ok(myRanking);
        return ResponseEntity.ok(null);
    }
}
