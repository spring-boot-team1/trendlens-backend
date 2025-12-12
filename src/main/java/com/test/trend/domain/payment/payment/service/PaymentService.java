package com.test.trend.domain.payment.payment.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.trend.domain.payment.payment.dto.PaymentConfirmResponse;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmResponse;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;
import com.test.trend.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 결제(Payment) 도메인의 비즈니스 로직을 처리하는 서비스.
 * <p>
 * - 결제 요청 기록(PENDING)<br>
 * - Toss 결제 승인(confirm)<br>
 * - 승인/실패 상태 변경 및 구독 갱신 연동<br>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository repository;
    private final UserSubscriptionService subscriptionService;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    /**
     * 결제 요청을 PENDING 상태로 기록
     */
    public Payment recordPaymentRequest(Long seqAccount, String orderId, Long amount) {
        Payment pending = Payment.createPending(seqAccount, orderId, amount);
        return repository.save(pending);
    }

    /**
     * Toss 결제 승인 + 구독 처리 (프론트 최종 연결용)
     */
    public PaymentConfirmResponse confirmTossPaymentAndSubscribe(
            TossPaymentConfirmRequest request
    ) {
        // 1. Toss 결제 승인 호출
        TossPaymentConfirmResponse tossResponse = requestTossConfirm(request);

        log.info("[TOSS CONFIRM RESPONSE] {}", tossResponse);

        // 2. 기존 PENDING Payment 조회
        Payment payment = repository.findByOrderId(request.getOrderId())
                .orElseThrow(() ->
                        new IllegalStateException("결제 요청(PENDING)을 찾을 수 없습니다.")
                );

        // 3. Toss 응답을 Payment 엔티티에 반영
        payment.applyTossConfirm(tossResponse);

        // 4. 결제 성공 시 구독 생성/연장
        UserSubscription subscription = null;
        if (payment.getPaymentStatus() == PaymentStatus.DONE) {
            subscription = subscriptionService.processPayment(payment);
            payment.linkSubscription(subscription.getSeqUserSub());
        }

        // 5. 프론트 전용 응답 DTO 반환
        return PaymentConfirmResponse.builder()
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getPaymentStatus().name())
                .nextBillingDate(
                        subscription != null ? subscription.getNextBillingDate() : null
                )
                .build();
    }

    /**
     * Toss Payments confirm API 호출
     */
    private TossPaymentConfirmResponse requestTossConfirm(
            TossPaymentConfirmRequest request
    ) {
        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes());

        return WebClient.create("https://api.tosspayments.com")
                .post()
                .uri("/v1/payments/confirm")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .blockOptional()
                .orElseThrow(() ->
                        new IllegalStateException("Toss 결제 승인 응답이 null입니다.")
                );
    }
}

