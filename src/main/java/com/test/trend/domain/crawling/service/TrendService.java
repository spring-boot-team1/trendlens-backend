package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.insight.WeeklyInsight;
import com.test.trend.domain.crawling.insight.WeeklyInsightRepository;
import com.test.trend.domain.crawling.interest.AccountKeyword;
import com.test.trend.domain.crawling.interest.AccountKeywordRepository;
import com.test.trend.domain.crawling.interest.InsightResponseDto;
import com.test.trend.domain.crawling.interest.TrendResponseDto;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.score.TrendScore;
import com.test.trend.domain.crawling.score.TrendScoreRepository;
import com.test.trend.domain.crawling.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // 상위 5위만 가져오기
        List<TrendScore> scores = trendScoreRepo.findDailyRank(LocalDate.now(), PageRequest.of(0, 5));
        return convertDto(scores);
    }

    // 3. 로그인 회원용 관심 키워드 랭킹 조회
    @Transactional(readOnly = true)
    public List<TrendResponseDto> getAccountRanks(Long seqAccount) {
        // 1. 회원의 관심 키워드 리스트 조회
        List<Keyword> myKeywords = accountKeywordRepo.findKeywordsBySeqAccount(seqAccount);

        if (myKeywords.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 해당 키워드들의 오늘자 TrendScore 조회
        List<TrendScore> scores = trendScoreRepo.findByKeywordInAndBaseDate(myKeywords, LocalDate.now());

        // 3. DTO 변환 후 반환
        return convertDto(scores);
    }

    // DTO 변환 공통 메서드
    private List<TrendResponseDto> convertDto(List<TrendScore> scores) {
        return scores.stream()
                .map(ts -> {
                    // 1. 현재 점수
                    int currentScore = (int) Math.round(ts.getFinalScore());

                    // 2. 지난주 점수 (DB에 과거 데이터가 없으므로 시뮬레이션 로직 적용)
                    // (나중에 DB에 데이터가 쌓이면 repo.findYesterdayScore(...)로 교체하면 됩니다)
                    double fluctuation = 0.8 + (Math.random() * 0.4); // 0.8 ~ 1.2 배수
                    long prevScore = (long) (currentScore * fluctuation);

                    // 3. 상승률 계산
                    double growthRate = 0.0;
                    if (prevScore > 0) {
                        growthRate = ((double) (currentScore - prevScore) / prevScore) * 100;
                    }

                    // 4. 상태 결정
                    String status = "stable";
                    if (growthRate > 5.0) status = "up";
                    else if (growthRate < -5.0) status = "down";

                    // 5. 요약 멘트 생성
                    String aiSummary = ts.getKeyword().getKeyword() + " 키워드의 검색량이 변동하고 있습니다.";

                    return TrendResponseDto.builder()
                            .seqKeyword(ts.getKeyword().getSeqKeyword())
                            .keyword(ts.getKeyword().getKeyword())
                            .category(ts.getKeyword().getCategory())
                            .trendScore(currentScore)
                            .prevScore(prevScore)          // 추가됨
                            .growthRate((double) Math.round(growthRate)) // 추가됨 (반올림)
                            .status(status)                // 추가됨
                            .aiSummary(aiSummary)          // 추가됨
                            .build();
                })
                .collect(Collectors.toList());
    }
}