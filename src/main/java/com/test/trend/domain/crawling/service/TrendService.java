package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.insight.WeeklyInsight;
import com.test.trend.domain.crawling.insight.WeeklyInsightRepository;
import com.test.trend.domain.crawling.interest.TrendResponseDto;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.score.TrendScore;
import com.test.trend.domain.crawling.score.TrendScoreRepository;
import com.test.trend.domain.crawling.util.DateUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrendService {

    private final KeywordRepository keywordRepo;
    private final WeeklyInsightRepository weeklyInsightRepo;
    private final TrendScoreRepository trendScoreRepo;
    private final TrendScoreRepository trendScoreRepository;


    public TrendService(KeywordRepository keywordRepo, WeeklyInsightRepository weeklyInsightRepository, TrendScoreRepository trendScoreRepository) {
        this.keywordRepo = keywordRepo;
        this.weeklyInsightRepo = weeklyInsightRepository;
        this.trendScoreRepository = trendScoreRepository;
        this.trendScoreRepo = trendScoreRepository;
    }

    @Transactional(readOnly = true)
    public List<TrendResponseDto> getTrendRanking() {

        //조회 기준 날짜 설정
        LocalDate targetDate = LocalDate.now();

        //키워드 상위 10위 조회
        List<TrendScore> scores = trendScoreRepo.findDailyRank(targetDate, PageRequest.of(0,10));

        //TrendScore -> TrendResponseDTO 변환
        return scores.stream()
                .map(ts -> TrendResponseDto.builder()
                        .seqKeyword(ts.getKeyword().getSeqKeyword())
                        .keyword(ts.getKeyword().getKeyword())
                        .category(ts.getKeyword().getCategory())
                        .trendScore((int) Math.round(ts.getFinalScore()))
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public String getWeeklyInsight(String keywordStr) {

        Keyword keyword = keywordRepo.findByKeyword(keywordStr)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 키워드입니다."));

        //2. 이번주 코드 생성
        String weekCode = DateUtil.currentWeekCode();

        //3. 리포트 조회
        Optional<WeeklyInsight> insightOpt = weeklyInsightRepo.findByKeywordAndWeekCode(keyword, weekCode);

        if (insightOpt.isPresent()) {
            return insightOpt.get().getSummaryTxt() + " ||| " + insightOpt.get().getStylingTip();
        } else {
            return "아직 분석된 데이터가 없습니다. (분석 요청 필요)";
        }

    }
}
