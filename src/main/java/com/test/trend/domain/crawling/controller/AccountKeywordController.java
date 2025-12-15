package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.interest.AccountKeywordService;
import com.test.trend.domain.crawling.interest.TrendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interests")
@RequiredArgsConstructor
public class AccountKeywordController {

    private final AccountKeywordService accountKeywordService;

    // 1. 관심 키워드 토글 (하트 클릭 시 호출)
    // URL: POST http://localhost:8080/api/v1/interests/toggle?seqAccount=1&keywordId=3
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleInterest(
            @RequestParam Long seqAccount, // 로그인한 유저 ID (나중엔 토큰에서 추출)
            @RequestParam Long seqKeyword) {

        boolean isRegistered = accountKeywordService.toggleInterest(seqAccount, seqKeyword);

        if (isRegistered) {
            return ResponseEntity.ok("관심 키워드로 등록되었습니다. (하트 켜짐)");
        } else {
            return ResponseEntity.ok("관심 키워드가 해제되었습니다. (하트 꺼짐)");
        }
    }

    // 2. 내 관심 목록 조회 (마이페이지 등)
    // URL: GET http://localhost:8080/api/v1/interests/my?seqAccount=1
    @GetMapping("/my")
    public ResponseEntity<List<TrendResponseDto>> getMyInterests(@RequestParam Long seqAccount) {
        List<TrendResponseDto> myInterests = accountKeywordService.getMyInterests(seqAccount);
        return ResponseEntity.ok(myInterests);
    }
}
