package com.test.trend;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;

@SpringBootTest
@Transactional
public class SubscriptionPlanRepositoryTest {

	@Autowired
	private SubscriptionPlanRepository repository;
	
	@Test
	void createSubscriptionPlan() {
		// given
		SubscriptionPlan plan = SubscriptionPlan.builder()
				.seqAccount(1L)
				.planName("Standard")
				.planDescription("기본 구독 플랜")
				.monthlyFee(9900)
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

	@Test
	void findSubscriptionPlan() {
		SubscriptionPlan plan = repository.save(
				SubscriptionPlan.builder()
								.seqAccount(1L)
								.planName("Premium")
								.planDescription("프리미엄 플랜")
								.monthlyFee(19900)
								.durationMonth(1)
								.status("ACTIVE")
								.createdAt(LocalDateTime.now())
								.build()
		);
		
		SubscriptionPlan found = repository.findById(plan.getSeqSubscriptionPlan()).orElse(null);
		
		assertThat(found).isNotNull();
		assertThat(found.getPlanName()).isEqualTo("Premium");				
		
	}
	
}


