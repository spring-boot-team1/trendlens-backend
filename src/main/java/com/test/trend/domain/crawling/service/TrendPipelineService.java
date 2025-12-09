package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.freq.WordFrequencyService;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.keyword.RisingKeywordDto;
import com.test.trend.domain.crawling.targeturl.SearchResultDto;
import com.test.trend.domain.crawling.targeturl.TargetUrl;
import com.test.trend.domain.crawling.targeturl.TargetUrlRepository;
import com.test.trend.enums.TargetUrlStatus;
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
    private final TargetUrlRepository targetUrlRepo;
    private final WordFrequencyService wordFrequencyService;

    public void runCrawlingFlow() {

        // 1) 무신사에서 (키워드 + 카테고리) 목록 가져오기
        List<RisingKeywordDto> risingKeywords = musinsaService.crawlRisingKeywords();

        if (risingKeywords.size() > 3) {
            risingKeywords = risingKeywords.subList(0, 3);
        }

        for (RisingKeywordDto rk : risingKeywords) {
            String keywordStr = rk.getKeyword();   // 예: "나이키 맨투맨"
            String category = rk.getCategory();    // 예: "상의"

            System.out.println(">>> [Pipeline] 키워드 처리 시작: " + keywordStr);

            // 2) Keyword 테이블에 저장 / 재사용 (ID 확보)
            Keyword keywordEntity = getOrCreateKeyword(keywordStr, category);
            Long seqKeyword = keywordEntity.getSeqKeyword();

            // 3) 네이버 블로그 검색 (URL 확보)
            List<SearchResultDto> searchResults = searchApiService.searchBlogUrls(keywordStr);

            // 4) 병렬 처리로 여러 블로그 동시에
            searchResults.parallelStream().forEach(dto -> {

                try {
                    processSingleContent(keywordEntity, dto);
                } catch (Exception e) {
                    System.out.println("  --> [SKIP] 에러:" + e.getMessage());
                }
            });

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
        System.out.println(">>>>[PipeLine] 모든 작업 완료");
    }

    private void processSingleContent(Keyword keywordEntity, SearchResultDto dto) {

        TargetUrl targetUrl;
        try {
            //[STEP A] TargetUrl 테이블에 먼저 저장(메타데이터)
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
            return;
        }

        //[STEP B] 상세 크롤링 수행
        JsoupCrawlerService.CrawledResult result = jsoupService.verifyAndGetContent(dto.url());

        if (result != null && result.content() != null && ! result.content().isBlank()) {

            //[STEP C] ContentDetail 저장 (TargetUrl과 연결)
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

            //[STEP D] TargetUrl 상태 업데이터(완료)
            targetUrl.setStatus(TargetUrlStatus.CRAWLED);
            targetUrlRepo.save(targetUrl);

            //[Step E] 단어 분석 (키워드 ID 전달)
            try {
                wordFrequencyService.analyzeAndSave(content, result.content());
            } catch (Exception e) {
                System.out.println(" --> 단어 분석 중 출동 발생(무시함)");
            }
        } else {
            //크롤링 실패시 상태 변겅
            targetUrl.setStatus(TargetUrlStatus.FAILED);
            targetUrlRepo.save(targetUrl);
        }

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

    @Transactional
    public void runWordFrqBath() {

        //아직 분석 안 된 본문만 가져오기
        List<ContentDetail> targets =
                contentDetailRepo.findByAnalyzedYn(YesNo.N);

        for (ContentDetail cd : targets) {
            try {
                wordFrequencyService.analyzeAndSave(cd, cd.getBodyText());
            } catch (Exception e) {
                System.out.println("[WordFreq] 분석 실패 seqContentDetail=" + cd.getSeqDetail());
            }
        }
    }
}