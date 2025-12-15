package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.service.FinalTrendPipelineService;
import com.test.trend.domain.crawling.service.TrendPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Pipeline", description = "데이터 파이프라인 수동 실행 (관리자용)")
@RestController
@RequestMapping("/api/admin/pipeline")
@RequiredArgsConstructor
public class AdminPipelineController {

    private final FinalTrendPipelineService finalTrendPipelineService;

    @Operation(summary = "전체 파이프라인 즉시 실행", description = "크롤링 -> 분석 -> 점수 -> AI생성 전 과정을 순차적으로 실행합니다.")
    @PostMapping("/run")
    public ResponseEntity<String> runFullPipeline() {
        // 지금은 결과 확인을 위해 동기로 실행
        try {
            finalTrendPipelineService.executeFullPipeline();
            return ResponseEntity.ok("✅ 전체 파이프라인 실행이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ 실행 실패: " + e.getMessage());
        }
    }
}
