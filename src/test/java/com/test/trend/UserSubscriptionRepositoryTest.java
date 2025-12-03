package com.test.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;

@SpringBootTest
@Transactional
public class UserSubscriptionRepositoryTest {

	@Autowired
	private UserSubscriptionRepository repository;
	
	@Test
	void createUserSubscription() {
		UserSubscription sub = UserSubscription.builder()
				.seqAccount(1L)
				.seqSubscriptionPlan(10L)
				.startDate(LocalDateTime.now())
				.endDate(LocalDateTime.now().plusMonths(1))
				.nextBillingDate(LocalDateTime.now().plusMonths(1))
				.autoRenewYn("Y")
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();
		
		UserSubscription saved = repository.save(sub);
		
		assertThat(saved.getSeqUserSub()).isNotNull();
		assertThat(saved.getAutoRenewYn()).isEqualTo("Y");
		
	}
	
	
}
