package com.test.trend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.mapper.UserSubscriptionMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;

class UserSubscriptionServiceTest {

    @Mock UserSubscriptionRepository userRepo;
    @Mock SubscriptionPlanRepository planRepo;
    @Mock UserSubscriptionMapper mapper;

    @InjectMocks UserSubscriptionService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 결제승인시_구독갱신_성공() {

        // given
        Payment payment = Payment.builder()
                .seqAccount(1L)
                .build();

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .durationMonth(1)   // ✅ 필수
                .build();

        UserSubscription subscription = UserSubscription.builder()
                .seqAccount(1L)
                .seqSubscriptionPlan(plan)
                .startDate(java.time.LocalDateTime.now()) // ✅ 필수
                .build();

        when(userRepo.findActiveBySeqAccount(1L))
                .thenReturn(java.util.Optional.of(subscription));

        // when
        service.processPayment(payment);

        // then
        assertThat(subscription.getNextBillingDate()).isNotNull();
    }
}

