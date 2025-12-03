package com.test.trend.domain.payment.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.payment.entity.UserBehaviorLog;

public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Long> {

}
