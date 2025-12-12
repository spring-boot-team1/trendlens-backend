package com.test.trend.domain.analyze.repository;

import com.test.trend.domain.analyze.entity.BodyRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyRecommendationRepository extends JpaRepository<BodyRecommendation, Long> {
}
