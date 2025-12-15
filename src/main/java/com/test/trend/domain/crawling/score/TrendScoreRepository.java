package com.test.trend.domain.crawling.score;

import com.test.trend.domain.crawling.keyword.Keyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrendScoreRepository extends JpaRepository<TrendScore, Long>{

    Optional<TrendScore> findByKeywordAndBaseDate(Keyword keyword, LocalDate baseDate);

    @Query("SELECT MAX(ts.baseDate) FROM TrendScore ts")
    LocalDate findLatestBaseDate();

    @Query("SELECT ts FROM TrendScore ts JOIN FETCH ts.keyword WHERE ts.baseDate = :date ORDER BY ts.finalScore DESC")
    List<TrendScore> findDailyRank(@Param("date") LocalDate date, Pageable pageable);

    List<TrendScore> findByKeywordInAndBaseDate(List<Keyword> myKeywords, LocalDate now);
}
