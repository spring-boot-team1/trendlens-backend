package com.test.trend.domain.payment.trend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.trend.entity.TrendSnapshot;

public interface TrendSnapshotRepository extends JpaRepository<TrendSnapshot, Long> {

	Optional<TrendSnapshot> findTopByOrderBySnapshotDateDesc();


}
