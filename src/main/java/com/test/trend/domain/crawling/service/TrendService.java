package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.insight.WeeklyInsight;
import com.test.trend.domain.crawling.insight.WeeklyInsightRepository;
import com.test.trend.domain.crawling.interest.AccountKeywordRepository;
import com.test.trend.domain.crawling.insight.InsightResponseDto;
import com.test.trend.domain.crawling.interest.TrendResponseDto;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.score.TrendScore;
import com.test.trend.domain.crawling.score.TrendScoreRepository;
import com.test.trend.domain.crawling.util.DateUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendService {

    private final KeywordRepository keywordRepo;
    private final WeeklyInsightRepository weeklyInsightRepo;
    private final TrendScoreRepository trendScoreRepo;
    private final AccountKeywordRepository accountKeywordRepo;


    @Transactional(readOnly = true)
    public List<InsightResponseDto> getWeeklyInsight(String searchKeyword) {

        // A. 점수 높은 순으로 연관 키워드 10개 가져오기
        List<Keyword> searchResults = keywordRepo.findBestMatchByScore(searchKeyword, PageRequest.of(0, 10));

        // B. 만약 점수 높은 게 하나도 없으면? -> 이름으로라도 검색 (Fallback)
        if (searchResults.isEmpty()) {
            Keyword fallback = keywordRepo.findFirstByKeywordContaining(searchKeyword);
            if (fallback != null) {
                searchResults.add(fallback);
            }
        }

        // C. 진짜 아무것도 없으면 빈 리스트 반환
        if (searchResults.isEmpty()) {
            return Collections.emptyList();
        }

        // D. 찾아낸 키워드들을 하나씩 돌면서 DTO로 변환 (Insight 정보 포함)
        List<InsightResponseDto> responseList = new ArrayList<>();
        String weekCode = DateUtil.currentWeekCode();

        for (Keyword k : searchResults) {
            // 이 키워드에 대한 AI 분석 데이터가 있는지 확인
            Optional<WeeklyInsight> insightOpt = weeklyInsightRepo.findByKeywordAndWeekCode(k, weekCode);

            InsightResponseDto dto;

            if (insightOpt.isPresent()) {
                // 분석 데이터 있음
                dto = InsightResponseDto.builder()
                        .seqKeyword(k.getSeqKeyword())
                        .keyword(k.getKeyword())
                        .category(k.getCategory())
                        .imgUrl(k.getImgUrl())
                        .summary(insightOpt.get().getSummaryTxt())
                        .stylingTip(insightOpt.get().getStylingTip())
                        .hasInsight(true) // 프론트에서 "분석됨" 표시 가능
                        .build();
            } else {
                // 분석 데이터 없음 (키워드는 존재하지만 AI가 안 돌음)
                dto = InsightResponseDto.builder()
                        .seqKeyword(k.getSeqKeyword())
                        .keyword(k.getKeyword())
                        .category(k.getCategory())
                        .summary("분석 대기 중입니다.")
                        .stylingTip(null)
                        .hasInsight(false) // 프론트에서 "분석 요청" 버튼 등을 띄울 수 있음
                        .build();
            }
            responseList.add(dto);
        }

        return responseList;
    }

    // 2. 비로그인용 Top 5 조회
    @Transactional(readOnly = true)
    public List<TrendResponseDto> getGuestTop5() {

        LocalDate targetDate = trendScoreRepo.findLatestBaseDate();

        // 데이터가 아예 없으면 빈 리스트 반환 (반어 코드)
        if (targetDate == null) {
            log.warn("[GuestTop5] TrendScore 테이블에 데이터 없음");
            return Collections.emptyList();
        }

        log.info("[GuestTop5] 조회 기준 날짜: {}", targetDate);

        // 3️⃣ 오늘 날짜 기준 TOP 5 조회
        List<TrendScore> scores =
                trendScoreRepo.findDailyRank(
                        targetDate,
                        PageRequest.of(0, 10)
                );

        log.info("[GuestTop5] daily rank size={}", scores.size());

        // 4️⃣ DTO 변환
        List<TrendResponseDto> result = convertDto(scores);
        log.info("[GuestTop5] result size={}", result.size());

        return result;
    }


    // 3. 로그인 회원용 관심 키워드 랭킹 조회
    @Transactional(readOnly = true)
    public List<TrendResponseDto> getAccountRanks(Long seqAccount) {
        // 1. 회원의 관심 키워드 리스트 조회
        List<Keyword> myKeywords = accountKeywordRepo.findKeywordsBySeqAccount(seqAccount);

        if (myKeywords.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 조회 기준 날짜 설정
        LocalDate targetDate = trendScoreRepo.findLatestBaseDate();
        if(targetDate == null) return Collections.emptyList();

        //3. 관심 키워드들의 점수 데이터 조회
        List<TrendScore> scores = trendScoreRepo.findByKeywordInAndBaseDate(myKeywords, targetDate);

        scores.sort((o1, o2) -> Double.compare(o2.getFinalScore(), o1.getFinalScore()));

        // 4. DTO 변환 후 반환
        return convertDto(scores);
    }

    // DTO 변환 공통 메서드
    // DTO 변환 공통 메서드
    private List<TrendResponseDto> convertDto(List<TrendScore> scores) {
        return scores.stream()
                .map(ts -> {
                    // 1. 현재 점수
                    int currentScore = (int) Math.round(ts.getFinalScore());

                    // 2. 지난주 점수 (DB 값 사용)
                    // null이면 0으로 처리
                    long prevScore = (ts.getPrevScore() != null) ? ts.getPrevScore() : 0L;

                    // 3. 상승률 (DB 값 사용)
                    // null이면 0.0으로 처리
                    double growthRate = (ts.getGrowthRate() != null) ? ts.getGrowthRate() : 0.0;

                    // 4. 상태 (DB 값 사용)
                    // null이면 "stable"로 처리
                    String status = (ts.getStatus() != null) ? ts.getStatus() : "stable";

                    // 5. 요약 멘트 생성
                    // 실제 데이터를 반영한 멘트로 변경
                    String trendDescription = (growthRate > 0) ? "상승" : (growthRate < 0 ? "하락" : "유지");
                    String aiSummary = String.format("%s 키워드는 전일 대비 %.1f%% %s하는 추세입니다.",
                            ts.getKeyword().getKeyword(), Math.abs(growthRate), trendDescription);

                    return TrendResponseDto.builder()
                            .seqKeyword(ts.getKeyword().getSeqKeyword())
                            .keyword(ts.getKeyword().getKeyword())
                            .category(ts.getKeyword().getCategory())
                            .trendScore(currentScore)

                            // 👇 계산 로직 없이 DB 값 그대로 매핑
                            .prevScore(prevScore)
                            .growthRate(growthRate)
                            .status(status)

                            .aiSummary(aiSummary)
                            .imgUrl(ts.getKeyword().getImgUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }
}