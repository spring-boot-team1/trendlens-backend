package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.TrendSnapshot;

public interface TrendSnapshotRepository extends JpaRepository<TrendSnapshot, Long> {

}
