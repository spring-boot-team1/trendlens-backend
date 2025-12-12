package com.test.trend.domain.payment.payment.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmResponse;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.mapper.PaymentMapper;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
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
    private final PaymentMapper mapper;
    private final UserSubscriptionService subscriptionService;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    /**
     * 결제 요청을 PENDING 상태로 먼저 기록한다.
     *
     * @param dto 결제 요청 정보
     * @return 저장된 결제 정보
     */
    public PaymentDTO recordPaymentRequest(PaymentDTO dto) {
        // 도메인 메서드 사용 (Payment.createPending)
        Payment pending = Payment.createPending(dto.getSeqAccount(), dto.getOrderId(), dto.getAmount());
        Payment saved = repository.save(pending);
        return mapper.toDto(saved);
    }

    /**
     * Toss Payments 결제 승인 API를 호출하고,
     * 응답 정보를 기반으로 Payment 엔티티를 생성/저장한 뒤 구독을 갱신한다.
     *
     * @param request Toss 결제 승인 요청 DTO
     * @return 저장된 Payment 정보
     */
    public PaymentDTO confirmTossPayment(TossPaymentConfirmRequest request) {

        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes());

        // 1) Toss REST API 호출 (결제 승인)
        TossPaymentConfirmResponse tossResponse = WebClient.create("https://api.tosspayments.com")
                .post()
                .uri("/v1/payments/confirm")
                .header("Authorization", auth)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();

        if (tossResponse == null) {
            throw new IllegalStateException("Toss 결제 승인 응답이 null입니다.");
        }

        log.info("[TOSS CONFIRM RESPONSE] {}", tossResponse);

        // 2) Toss 응답 → Payment 엔티티 생성
        Payment payment = Payment.builder()
                .paymentKey(tossResponse.getPaymentKey())
                .orderId(tossResponse.getOrderId())
                .seqAccount(request.getSeqAccount())
                .amount(tossResponse.getTotalAmount())
                .paymentMethod(tossResponse.getMethod())
                .paymentStatus(PaymentStatus.fromTossStatus(tossResponse.getStatus()))
                .approveTime(parseTossTime(tossResponse.getApprovedAt()))
                .build();

        Payment saved = repository.save(payment);

        // 3) 결제 성공(DONE)인 경우 구독 갱신 처리
        if (saved.getPaymentStatus() == PaymentStatus.DONE) {
            subscriptionService.processPayment(saved);
        }

        // 4) DTO 변환 후 반환
        return mapper.toDto(saved);
    }

    private LocalDateTime parseTossTime(String time) {
        // Toss 응답의 ISO-8601 문자열 → LocalDateTime 변환
        return OffsetDateTime.parse(time).toLocalDateTime();
    }
}
