package com.test.trend.domain.analyze.repository;

import com.test.trend.domain.analyze.entity.BodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
}
