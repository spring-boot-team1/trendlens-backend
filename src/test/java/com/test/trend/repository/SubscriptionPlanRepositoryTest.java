package com.test.trend.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;

@DataJpaTest
@Transactional
public class SubscriptionPlanRepositoryTest {

	@Autowired
	private SubscriptionPlanRepository repository;
	
	@Test
	void createSubscriptionPlan() {
		// given
		SubscriptionPlan plan = SubscriptionPlan.builder()
				.planName("Standard")
				.planDescription("기본 구독 플랜")
				.monthlyFee(9900L)
				.durationMonth(1)
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();
		
		// when
		SubscriptionPlan saved = repository.save(plan);
		
		// then
		assertThat(saved.getSeqSubscriptionPlan()).isNotNull();
		assertThat(saved.getPlanName()).isEqualTo("Standard");
		
	}
	
}


