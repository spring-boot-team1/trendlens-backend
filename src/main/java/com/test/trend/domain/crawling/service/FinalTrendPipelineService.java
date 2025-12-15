package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.freq.WordFrequencyService;
import com.test.trend.domain.crawling.insight.WeeklyInsightService;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.keyword.RisingKeywordDto;
import com.test.trend.domain.crawling.score.TrendScoreService;
import com.test.trend.domain.crawling.targeturl.SearchResultDto;
import com.test.trend.domain.crawling.targeturl.TargetUrl;
import com.test.trend.domain.crawling.targeturl.TargetUrlRepository;
import com.test.trend.enums.TargetUrlStatus;
import com.test.trend.enums.YesNo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinalTrendPipelineService {

    // --- Services ---
    private final MusinsaCategoryCrawlerService musinsaService;
    private final SearchApiService searchApiService;
    private final JsoupCrawlerService jsoupService;
    private final WordFrequencyService wordFrequencyService;
    private final DataLabApiService dataLabApiService;
    private final TrendScoreService trendScoreService;
    private final WeeklyInsightService weeklyInsightService;

    // --- Repositories ---
    private final ContentDetailRepository contentDetailRepo;
    private final KeywordRepository keywordRepo;
    private final TargetUrlRepository targetUrlRepo;

    /**
     * 🚀 [메인 파이프라인] 전체 데이터 수집 및 분석 프로세스 실행
     * 스케줄러나 관리자 컨트롤러에서 이 메서드만 호출하면 됩니다.
     */
    public void executeFullPipeline() {
        long startTime = System.currentTimeMillis();
        log.info("========== [Pipeline] Daily Trend Analysis Started ==========");

        // 1. 무신사 크롤링 (키워드 발굴)
        log.info(">>> [Step 1] Crawling Rising Keywords from Musinsa...");
        List<RisingKeywordDto> risingKeywords = musinsaService.crawlRisingKeywords();
        log.info("   -> Found {} keywords.", risingKeywords.size());

        // 2. 각 키워드별 상세 프로세스 (저장 -> 블로그수집 -> 분석 -> 데이터랩)
        log.info(">>> [Step 2~4] Processing Each Keyword...");
        int successCount = 0;
        for (RisingKeywordDto rk : risingKeywords) {
            try {
                processSingleKeywordFlow(rk);
                successCount++;
            } catch (Exception e) {
                // 키워드 하나가 실패해도 전체 파이프라인은 멈추지 않음
                log.error("   -> [Skip] Failed to process keyword: {}", rk.getKeyword(), e);
            }
        }
        log.info("   -> Processed {}/{} keywords successfully.", successCount, risingKeywords.size());

        // 5. 트렌드 점수 재계산 (전체 키워드 대상)
        log.info(">>> [Step 5] Recalculating Trend Scores...");
        try {
            trendScoreService.recalcTodayScores();
        } catch (Exception e) {
            log.error(">>> [TrendScore] Calculation Failed", e);
        }

        // 6. AI 인사이트 생성 (상위 키워드 대상)
        log.info(">>> [Step 6] Generating Weekly Insights (AI)...");
        generateInsightsForKeywords(risingKeywords);

        long endTime = System.currentTimeMillis();
        log.info("========== [Pipeline] Finished in {} ms ==========", (endTime - startTime));
    }

    /**
     * 개별 키워드 처리 로직 (복잡도 분리)
     * 키워드 저장 -> 블로그 URL 수집 -> 본문 크롤링 -> 데이터랩 지표
     */
    private void processSingleKeywordFlow(RisingKeywordDto rk) {
        String keywordStr = rk.getKeyword();
        log.info("   -> Processing: [{}] ({})", keywordStr, rk.getCategory());

        // 2-1) Keyword 테이블 저장/업데이트
        Keyword keywordEntity = getOrCreateKeyword(keywordStr, rk.getCategory(), rk.getImgUrl());

        // 2-2) 네이버 블로그 URL 수집
        List<SearchResultDto> searchResults = searchApiService.searchBlogUrls(keywordStr);

        // 2-3) 본문 크롤링 & 형태소 분석 (병렬 처리)
        // 병렬 스트림은 속도는 빠르지만 DB 커넥션을 많이 쓸 수 있으니 주의
        searchResults.parallelStream().forEach(dto -> {
            try {
                crawlAndAnalyzePost(keywordEntity, dto);
            } catch (Exception e) {
                log.debug("      -> [Blog Fail] {}", e.getMessage()); // 상세 로그는 debug 레벨로
            }
        });

        // 2-4) 데이터랩 지표 수집
        try {
            dataLabApiService.fetchAndSaveTrend(keywordEntity.getSeqKeyword());
        } catch (Exception e){
            log.warn("      -> [DataLab Fail] {}", e.getMessage());
        }

        // 2-5) Rate Limiting (차단 방지용 딜레이)
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    /**
     * 블로그 포스트 하나에 대한 크롤링 및 분석
     */
    private void crawlAndAnalyzePost(Keyword keywordEntity, SearchResultDto dto) {
        // [A] TargetUrl 저장 (중복 방지 로직은 Repo 레벨이나 Service 앞단에 있으면 좋음)
        TargetUrl targetUrl;
        try {
            targetUrl = TargetUrl.builder()
                    .keyword(keywordEntity)
                    .url(dto.url())
                    .title(dto.title())
                    .postDate(dto.postDate())
                    .domain("NAVER_BLOG")
                    .status(TargetUrlStatus.WAIT)
                    .build();
            targetUrl = targetUrlRepo.save(targetUrl);
        } catch (Exception e) {
            // 이미 존재하는 URL이거나 저장 실패 시 스킵
            return;
        }

        // [B] Jsoup 상세 크롤링
        JsoupCrawlerService.CrawledResult result = jsoupService.verifyAndGetContent(dto.url());

        if (result != null && result.content() != null && !result.content().isBlank()) {
            // [C] ContentDetail 저장
            ContentDetail content = ContentDetail.builder()
                    .targetUrl(targetUrl)
                    .bodyText(result.content())
                    .imageUrl(result.imageUrl())
                    .crawledAt(LocalDateTime.now())
                    .engineType("Jsoup")
                    .status("SUCCESS")
                    .analyzedYn(YesNo.N)
                    .build();
            contentDetailRepo.save(content);

            // [D] 상태 업데이트
            targetUrl.setStatus(TargetUrlStatus.CRAWLED);
            targetUrlRepo.save(targetUrl);

            // [E] 형태소 분석 (즉시 실행)
            try {
                wordFrequencyService.analyzeAndSave(content, result.content());
            } catch (Exception e) {
                log.warn("      -> Word Analysis Error: {}", e.getMessage());
            }
        } else {
            // 실패 처리
            targetUrl.setStatus(TargetUrlStatus.FAILED);
            targetUrlRepo.save(targetUrl);
        }
    }

    /**
     * AI 인사이트 생성 (일괄 처리)
     */
    private void generateInsightsForKeywords(List<RisingKeywordDto> keywords) {
        for (RisingKeywordDto rk : keywords) {
            try {
                Keyword entity = keywordRepo.findByKeyword(rk.getKeyword()).orElse(null);
                if (entity != null) {
                    weeklyInsightService.createWeeklyInsight(entity);
                }
            } catch (Exception e) {
                log.error("   -> [Insight Fail] {}", rk.getKeyword(), e);
            }
        }
    }

    /**
     * 키워드 엔티티 조회 또는 생성
     */
    private Keyword getOrCreateKeyword(String keywordStr, String category, String imgUrl) {
        return keywordRepo.findByKeyword(keywordStr)
                .map(existing -> {
                    boolean updated = false;
                    if (category != null && !category.equals("기타") && !category.equals(existing.getCategory())) {
                        existing.setCategory(category);
                        updated = true;
                    }
                    if (imgUrl != null && !imgUrl.isBlank() && !imgUrl.equals(existing.getImgUrl())) {
                        existing.setImgUrl(imgUrl);
                        updated = true;
                    }
                    return updated ? keywordRepo.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Keyword k = new Keyword();
                    k.setKeyword(keywordStr);
                    k.setCategory(category);
                    k.setImgUrl(imgUrl);
                    k.setIsActive(YesNo.Y);
                    return keywordRepo.save(k);
                });
    }

    /**
     * [유틸] 분석 안 된 데이터 수동 재처리용 (필요시 사용)
     */
    @Transactional
    public void retryFailedWordAnalysis() {
        List<ContentDetail> targets = contentDetailRepo.findByAnalyzedYn(YesNo.N);
        log.info(">>> Retrying word analysis for {} items...", targets.size());

        for (ContentDetail cd : targets) {
            try {
                wordFrequencyService.analyzeAndSave(cd, cd.getBodyText());
            } catch (Exception e) {
                log.error("   -> Retry failed for seq={}", cd.getSeqDetail());
            }
        }
    }
}