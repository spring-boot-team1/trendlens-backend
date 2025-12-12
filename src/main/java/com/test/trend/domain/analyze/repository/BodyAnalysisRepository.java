package com.test.trend.domain.analyze.repository;

import com.test.trend.domain.analyze.entity.BodyAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyAnalysisRepository extends JpaRepository<BodyAnalysis, Long> {
}
