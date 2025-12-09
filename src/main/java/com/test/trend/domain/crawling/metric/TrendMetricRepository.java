package com.test.trend.domain.crawling.metric;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TrendMetricRepository extends JpaRepository<TrendMetric, Long> {

    Optional<TrendMetric> findByKeyword_SeqKeywordAndBaseDate(Long seqKeyword, LocalDate parsedDate);

    Optional<TrendMetric> findByKeyword_SeqKeywordOrderByBaseDateDesc(Long seqKeyword);
}
