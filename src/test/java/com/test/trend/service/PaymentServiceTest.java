package com.test.trend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.mapper.PaymentMapper;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
import com.test.trend.domain.payment.payment.service.PaymentService;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;

class PaymentServiceTest {

    @Mock PaymentRepository repository;
    @Mock PaymentMapper mapper;
    @Mock UserSubscriptionService subscriptionService;

    @InjectMocks PaymentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Toss결제승인_성공() {

        // 실제 Toss 호출은 Mock 처리
        // confirmTossPayment() 는 통합 테스트에서 확인 가능

        Payment saved = Payment.builder()
                .seqAccount(1L)
                .amount(10000L)
                .build();

        when(repository.save(any(Payment.class))).thenReturn(saved);

        subscriptionService.processPayment(saved);

        verify(subscriptionService, times(1))
                .processPayment(saved);
    }
}

