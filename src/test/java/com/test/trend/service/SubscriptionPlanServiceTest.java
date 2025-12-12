package com.test.trend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.mapper.SubscriptionPlanMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;
import com.test.trend.domain.payment.subscription.service.SubscriptionPlanService;
import com.test.trend.enums.SubscriptionStatus;

class SubscriptionPlanServiceTest {

    @Mock
    SubscriptionPlanRepository repository;

    @Mock
    SubscriptionPlanMapper mapper;

    @InjectMocks
    SubscriptionPlanService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 구독상품_생성_성공() {

        SubscriptionPlanDTO dto = SubscriptionPlanDTO.builder()
                .planName("PRO")
                .planDescription("프로 플랜")
                .monthlyFee(20000L)
                .durationMonth(1)
                .status("ACTIVE")
                .build();

        SubscriptionPlan entity = SubscriptionPlan.builder()
                .planName("PRO")
                .planDescription("프로 플랜")
                .monthlyFee(20000L)
                .durationMonth(1)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        SubscriptionPlanDTO result = service.create(dto);

        assertThat(result.getPlanName()).isEqualTo("PRO");
    }
}


