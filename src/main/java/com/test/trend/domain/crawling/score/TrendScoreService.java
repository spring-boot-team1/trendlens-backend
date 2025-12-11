package com.test.trend.domain.crawling.score;

import com.test.trend.domain.crawling.freq.WordFrequencyRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.metric.TrendMetric;
import com.test.trend.domain.crawling.metric.TrendMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendScoreService {

    private final KeywordRepository keywordRepo;
    private final WordFrequencyRepository wordFreqRepo;
    private final TrendMetricRepository trendMetricRepo;
    private final TrendScoreRepository trendScoreRepo;

    /*
        오늘 기준 트렌드 점수 전체 재계산
        1. scoreA = WordFrequency 총합 기반 0~100 정규화
        2. scoreB = 최신 TrendMetric.ratio (0~100)
        3. FinalScore = A*0.4 + B*0.6
        4. finalScore DESC 기준 rank 부여
    */

    @Transactional
    public void recalcTodayScores(){
        LocalDate today = LocalDate.now();

        //1. wordFrequency 집계 (키워드별 총 합)
        List<WordFrequencyRepository.WordFreqAgg> aggs =
                wordFreqRepo.findKeywordTotalCounts();

        if (aggs.isEmpty()) {
            System.out.println("[TrendScore] wordFrequency 집계 결과가 비어있음");
            return;
        }

        //seqKeyword -> totalCount
        Map<Long, Long> totalCountMap = aggs.stream()
                .collect(Collectors.toMap(
                        WordFrequencyRepository.WordFreqAgg::getSeqKeyword,
                        WordFrequencyRepository.WordFreqAgg::getTotalCount
                ));

        Long maxCount = totalCountMap.values().stream()
                .mapToLong(v -> v)
                .max()
                .orElse(0L);

        List<TrendScore> scores = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : totalCountMap.entrySet()) {
            Long seqKeyword = entry.getKey();
            Long totalCount = entry.getValue();

            Keyword keyword = keywordRepo.findById(seqKeyword)
                    .orElse(null);

            if (keyword == null) continue;

            //ScoreA: 전체 중 이 키워드의 비율 (0~100)
            double scoreA = (maxCount == 0)
                    ? 0.0
                    : (double) totalCount / maxCount * 100.0;

            // ScoreB: 최신 TrendMetric ratio (없으면 0)
            Optional<TrendMetric> latestMetricOpt =
                    trendMetricRepo.findTopByKeyword_SeqKeywordOrderByBaseDateDesc(seqKeyword);

            double scoreB = latestMetricOpt
                    .map(TrendMetric::getRatio)
                    .orElse(0.0);

            // 기준 날짜: 최신 Metric 날짜가 있으면 그걸로, 없으면 오늘
            LocalDate baseDate = latestMetricOpt
                    .map(TrendMetric::getBaseDate)
                    .orElse(today);

            double finalScore = scoreA * 0.4 + scoreB * 0.6;

            TrendScore score = trendScoreRepo
                    .findByKeywordAndBaseDate(keyword, baseDate)
                    .orElseGet(() -> TrendScore.builder()
                            .keyword(keyword)
                            .baseDate(baseDate)
                            .createdAt(LocalDateTime.now())
                            .build()
                    );

            score.setScoreA(scoreA);
            score.setScoreB(scoreB);
            score.setFinalScore(finalScore);

            scores.add(score);
        }

        //3. finalScore DESC 기준 rank 부여
        scores.sort(Comparator.comparing(TrendScore::getFinalScore).reversed());

        int rank = 1;
        for (TrendScore s : scores) {
            s.setRank(rank++);
        }

        trendScoreRepo.saveAll(scores);

        System.out.println("[TrendScore]" + scores.size() + "건 점수/랭킹 계산완료");



    }
}
