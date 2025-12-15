package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.keyword.RisingKeywordDto;
import com.test.trend.domain.crawling.service.MusinsaCategoryCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/crawling")
public class MusinsaCategoryTestController {

    private final MusinsaCategoryCrawlerService musinsaCategoryCrawlerService;

    /**
     * 무신사 카테고리 크롤러 단독 테스트용 엔드포인트
     * - 파이프라인 안 타고, 이 메서드만 호출해서 로그 + 결과 확인
     */
    @GetMapping("/musinsa")
    public List<RisingKeywordDto> testMusinsaCategoryCrawler() {
        // 여기서 콘솔 로그는 MusinsaCategoryCrawlerService 안의 System.out.println 으로 찍힘
        return musinsaCategoryCrawlerService.crawlRisingKeywords();
    }
}
