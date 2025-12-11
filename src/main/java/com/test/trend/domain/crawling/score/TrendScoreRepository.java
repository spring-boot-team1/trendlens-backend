package com.test.trend.domain.crawling.score;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.metric.TrendMetric;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TrendScoreRepository extends JpaRepository<TrendScore, Long>{

    Optional<TrendScore> findByKeywordAndBaseDate(Keyword keyword, LocalDate baseDate);
}
