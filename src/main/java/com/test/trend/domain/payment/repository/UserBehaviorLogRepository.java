package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.UserBehaviorLog;

public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Long> {

}
