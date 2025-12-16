package com.test.trend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSubscriptionRepositoryTest {

    @Autowired
    private UserSubscriptionRepository repository;

    @Test
    void 사용자구독_저장_조회() {

        UserSubscription sub = UserSubscription.builder()
                .seqAccount(1L)
                .startDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        UserSubscription saved = repository.save(sub);

        UserSubscription found =
                repository.findById(saved.getSeqUserSub()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getSeqAccount()).isEqualTo(1L);
    }
}

