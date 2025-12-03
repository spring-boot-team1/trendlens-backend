package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.UserTrendHistory;

public interface UserTrendHistoryRepository extends JpaRepository<UserTrendHistory, Long> {

}
