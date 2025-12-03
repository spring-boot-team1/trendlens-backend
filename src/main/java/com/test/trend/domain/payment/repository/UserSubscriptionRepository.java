package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

}
