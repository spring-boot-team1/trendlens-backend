package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.freq.WordFrequencyService;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.keyword.RisingKeywordDto;
import com.test.trend.domain.crawling.targeturl.SearchResultDto;
import com.test.trend.enums.YesNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendPipelineService {

    private final MusinsaCategoryCrawlerService musinsaService;
    private final SearchApiService searchApiService;
    private final JsoupCrawlerService jsoupService;
    private final ContentDetailRepository contentDetailRepo;
    private final KeywordRepository keywordRepo;
    private final WordFrequencyService wordFrequencyService;

    @Transactional
    public void runCrawlingFlow() {

        // 1) 무신사에서 (키워드 + 카테고리) 목록 가져오기
        List<RisingKeywordDto> risingKeywords = musinsaService.crawlRisingKeywords();

        risingKeywords = risingKeywords.stream()
                .limit(5)
                .toList();

        for (RisingKeywordDto rk : risingKeywords) {

            String keywordStr = rk.getKeyword();   // 예: "나이키 맨투맨"
            String category = rk.getCategory();    // 예: "상의"

            System.out.println(">>> [Pipeline] 키워드 처리 시작: " + keywordStr);

            // 2) Keyword 테이블에 저장 / 재사용 (ID 확보)
            Keyword keywordEntity = getOrCreateKeyword(keywordStr, category);
            Long seqKeyword = keywordEntity.getSeqKeyword();

            // 3) 네이버 블로그 검색 (URL 확보)
            List<SearchResultDto> searchResults = searchApiService.searchBlogUrls(keywordStr);

            // 여러 블로그를 동시에 크롤링합니다.
            searchResults.parallelStream().forEach(dto -> {
                try {
                    // 4) 상세 크롤링 (가장 오래 걸리는 작업)
                    JsoupCrawlerService.CrawledResult result = jsoupService.verifyAndGetContent(dto.url());

                    // 내용이 없거나 null이면 패스
                    if (result != null && result.content() != null && !result.content().isBlank()) {

                        // 5) ContentDetail 저장
                        ContentDetail content = ContentDetail.builder()
                                .keyword(keywordEntity)
                                .title(dto.title())
                                .originalUrl(dto.url())
                                .bodyText(result.content())
                                .imageUrl(result.imageUrl())
                                .crawledAt(LocalDateTime.now())
                                .engineType("SELENIUM")
                                .status("Y")
                                .analyzedYn(YesNo.N)
                                .build();

                        contentDetailRepo.save(content);
                    }
                } catch (Exception e) {
                    System.out.println("   -> 크롤링 실패 (건너뜀): " + dto.url());
                }
            });

            // 네이버 API 보호를 위해 키워드 간에는 살짝 대기 (0.5초)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println(">>> [Pipeline] 모든 작업 완료!");
    }


    // 키워드가 있으면 가져오고, 없으면 새로 저장하는 메서드
    private Keyword getOrCreateKeyword(String keywordStr, String category) {
        return keywordRepo.findByKeyword(keywordStr)
                .map(existing -> {
                    // 기존 키워드에 카테고리가 비어있으면 업데이트
                    if (existing.getCategory() == null && category != null) {
                        existing.setCategory(category);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    Keyword k = new Keyword();
                    k.setKeyword(keywordStr);
                    k.setCategory(category);  // 상의 / 하의 / 신발 등
                    k.setIsActive(YesNo.Y);
                    return keywordRepo.save(k);
                });
    }
}