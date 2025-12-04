package com.test.trend.domain.payment.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.log.entity.UserBehaviorLog;

public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Long> {

}
