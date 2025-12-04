package com.test.trend.domain.payment.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.trend.entity.UserTrendHistory;

public interface UserTrendHistoryRepository extends JpaRepository<UserTrendHistory, Long> {

}
