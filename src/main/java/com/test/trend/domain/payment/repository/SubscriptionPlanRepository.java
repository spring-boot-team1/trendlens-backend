package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.SubscriptionPlan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

}
