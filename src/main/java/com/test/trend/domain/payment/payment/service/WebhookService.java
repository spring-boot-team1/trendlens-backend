package com.test.trend.domain.payment.payment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.payment.dto.toss.TossWebhookRequest;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.entity.WebhookLog;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
import com.test.trend.domain.payment.payment.repository.WebhookLogRepository;
import com.test.trend.enums.PaymentStatus;
import com.test.trend.enums.YesNo;

import lombok.RequiredArgsConstructor;

/**
 * Toss Webhook 이벤트를 처리하는 서비스.
 * <p>
 * - Webhook 로그 저장<br>
 * - 시그니처 검증(컨트롤러에서 처리 후 위임 가능)<br>
 * - Payment 상태 동기화<br>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Webhook Payload를 파싱하고, 로그를 남기며, Payment 상태를 동기화한다.
     *
     * @param payload   Raw JSON 문자열
     * @param signature Toss Webhook 시그니처 헤더 값
     */
    public void processWebhook(String payload, String signature) {

        try {
            TossWebhookRequest request = objectMapper.readValue(payload, TossWebhookRequest.class);

            String eventType = request.getEventType();
            String paymentKey = request.getData().getPaymentKey();

            // 1) Webhook 로그 저장
            WebhookLog log = WebhookLog.builder()
                    .eventType(eventType)
                    .paymentKey(paymentKey)
                    .signature(signature)
                    .payload(payload)
                    .processed(YesNo.N)
                    .receivedAt(LocalDateTime.now())
                    .build();
            webhookLogRepository.save(log);

            // 2) Payment 조회
            Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                    .orElse(null);

            if (payment == null) {
                log.markSkipped("해당 paymentKey에 대한 Payment 없음", LocalDateTime.now());
                return;
            }

            // 3) 이벤트 타입별 상태 동기화
            switch (eventType) {
                case "PAYMENT_APPROVED" -> handleApproved(payment, log);
                case "PAYMENT_CANCELED" -> handleCanceled(payment, log);
                case "PAYMENT_EXPIRED"  -> handleExpired(payment, log);
                case "PAYMENT_FAILED"   -> handleFailed(payment, log);
                default -> log.markSkipped("지원하지 않는 eventType: " + eventType, LocalDateTime.now());
            }

        } catch (Exception e) {
            // 파싱 실패 등 예외가 발생한 경우 로그를 남기고 종료
            WebhookLog errorLog = WebhookLog.builder()
                    .eventType("UNKNOWN")
                    .paymentKey(null)
                    .signature(signature)
                    .payload(payload)
                    .processed(YesNo.N)
                    .note("Webhook 처리 중 예외 발생: " + e.getMessage())
                    .receivedAt(LocalDateTime.now())
                    .build();
            webhookLogRepository.save(errorLog);
        }
    }

    private void handleApproved(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.DONE);
        log.markProcessed("결제 승인 상태 동기화 완료", LocalDateTime.now());
    }

    private void handleCanceled(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.CANCELED);
        log.markProcessed("결제 취소 상태 동기화 완료", LocalDateTime.now());
    }

    private void handleExpired(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.EXPIRED);
        log.markProcessed("결제 만료 상태 동기화 완료", LocalDateTime.now());
    }

    private void handleFailed(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.FAILED);
        log.markProcessed("결제 실패 상태 동기화 완료", LocalDateTime.now());
    }
}
