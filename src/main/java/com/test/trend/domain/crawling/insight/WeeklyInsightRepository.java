package com.test.trend.domain.crawling.insight;

import com.test.trend.domain.crawling.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyInsightRepository extends JpaRepository<WeeklyInsight, Long>{

    Optional<WeeklyInsight> findByKeywordAndWeekCode(Keyword keyword, String weekCode);

    Optional<WeeklyInsight> findFirstByKeywordOrderByWeekCodeDesc(Keyword k);
}
