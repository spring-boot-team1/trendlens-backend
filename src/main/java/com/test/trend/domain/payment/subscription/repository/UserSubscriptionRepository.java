package com.test.trend.domain.payment.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.subscription.entity.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

	Optional<UserSubscription> findActiveBySeqAccount(Long seqAccount);




}
