package com.test.trend.domain.crawling.score;

import com.test.trend.domain.crawling.freq.WordFrequencyRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.metric.TrendMetric;
import com.test.trend.domain.crawling.metric.TrendMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        4. (New) 어제 점수와 비교하여 상승률 및 상태 결정
        5. finalScore DESC 기준 rank 부여
    */
    @Transactional
    public void recalcTodayScores() {
        LocalDate today = LocalDate.now();

        // 1. wordFrequency 집계 (키워드별 총 합)
        List<WordFrequencyRepository.WordFreqAgg> aggs = wordFreqRepo.findKeywordTotalCounts();

        if (aggs.isEmpty()) {
            log.info("[TrendScore] wordFrequency 집계 결과가 비어있음");
            return;
        }

        // seqKeyword -> totalCount
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

            Keyword keyword = keywordRepo.findById(seqKeyword).orElse(null);
            if (keyword == null) continue;

            // ScoreA: 전체 중 이 키워드의 비율 (0~100)
            double scoreA = (maxCount == 0) ? 0.0 : (double) totalCount / maxCount * 100.0;

            // ScoreB: 최신 TrendMetric ratio (없으면 0)
            Optional<TrendMetric> latestMetricOpt =
                    trendMetricRepo.findTopByKeyword_SeqKeywordOrderByBaseDateDesc(seqKeyword);

            double scoreB = latestMetricOpt.map(TrendMetric::getRatio).orElse(0.0);

            // 기준 날짜 설정
            LocalDate baseDate = latestMetricOpt.map(TrendMetric::getBaseDate).orElse(today);

            // 최종 점수 계산
            double finalScore = scoreA * 0.4 + scoreB * 0.6;

            // TrendScore 엔티티 생성 또는 조회
            TrendScore score = trendScoreRepo.findByKeywordAndBaseDate(keyword, baseDate)
                    .orElseGet(() -> TrendScore.builder()
                            .keyword(keyword)
                            .baseDate(baseDate)
                            .createdAt(LocalDateTime.now())
                            .build()
                    );

            score.setScoreA(scoreA);
            score.setScoreB(scoreB);
            score.setFinalScore(finalScore);

            // 🔥 [추가된 로직] 어제 점수 비교 및 상승률 계산
            calculateGrowth(score, keyword, baseDate, finalScore);

            scores.add(score);
        }

        // 3. finalScore DESC 기준 rank 부여
        scores.sort(Comparator.comparing(TrendScore::getFinalScore).reversed());

        int rank = 1;
        for (TrendScore s : scores) {
            s.setRank(rank++);
        }

        trendScoreRepo.saveAll(scores);
        log.info("[TrendScore] {}건 점수/랭킹/상승률 계산 완료", scores.size());
    }

    // 🔥 상승률 계산 메서드 분리
    private void calculateGrowth(TrendScore currentScoreEntity, Keyword keyword, LocalDate today, double currentFinalScore) {
        LocalDate yesterday = today.minusDays(1);

        // 어제 점수 가져오기
        Optional<TrendScore> prevScoreOpt = trendScoreRepo.findByKeywordAndBaseDate(keyword, yesterday);

        long currentScore = Math.round(currentFinalScore);
        long prevScore = prevScoreOpt.map(ts -> Math.round(ts.getFinalScore())).orElse(0L);
        double growthRate = 0.0;

        // 상승률 계산 공식
        if (prevScore > 0) {
            growthRate = ((double) (currentScore - prevScore) / prevScore) * 100.0;
        } else if (currentScore > 0) {
            growthRate = 100.0; // 어제 없었는데 오늘 생겼으면 100% 상승 (New)
        }

        // 상태 결정 (UP / DOWN / STABLE)
        String status = "stable";
        if (growthRate >= 5.0) status = "up";
        else if (growthRate <= -5.0) status = "down";

        // 엔티티에 값 주입
        currentScoreEntity.setPrevScore(prevScore);
        currentScoreEntity.setGrowthRate(Math.round(growthRate * 10.0) / 10.0); // 소수점 1자리 반올림
        currentScoreEntity.setStatus(status);
    }
}