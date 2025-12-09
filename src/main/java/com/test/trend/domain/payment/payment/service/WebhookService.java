package com.test.trend.domain.payment.payment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.payment.dto.toss.TossWebhookRequest;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.entity.WebhookLog;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
import com.test.trend.domain.payment.payment.repository.WebhookLogRepository;
import com.test.trend.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final WebhookLogRepository webhookLogRepository;

    /**
     * Webhook 처리
     * 1) WebhookLog 저장
     * 2) 이미 처리된 이벤트인지 중복 체크
     * 3) Payment 상태 동기화
     */
    @Transactional
    public void processWebhook(TossWebhookRequest request, String rawPayload, String signature) {

        String eventType = request.getEventType();
        String paymentKey = request.getData().getPaymentKey();

        // 1) Webhook 로그 저장 (수신 시각 기준)
        WebhookLog log = WebhookLog.builder()
                .eventType(eventType)
                .paymentKey(paymentKey)
                .signature(signature)
                .payload(rawPayload)
                .receivedAt(LocalDateTime.now())
                .build();
        webhookLogRepository.save(log);

        // 2) 중복 이벤트 체크
        boolean alreadyProcessed =
                webhookLogRepository.existsByPaymentKeyAndEventTypeAndProcessedTrue(paymentKey, eventType);

        if (alreadyProcessed) {
            log.markSkipped("이미 처리된 Webhook 이벤트");
            // JPA 영속 상태이므로 별도 save() 필요 X
            return;
        }

        // 3) Payment 조회
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElse(null);

        if (payment == null) {
            log.markSkipped("해당 paymentKey에 대한 Payment 엔티티 없음");
            return;
        }

        // 4) 이벤트 타입별 상태 동기화 (PaymentStatus만 변경)
        switch (eventType) {
            case "PAYMENT_APPROVED" -> handleApproved(payment, log);
            case "PAYMENT_CANCELED" -> handleCanceled(payment, log);
            case "PAYMENT_EXPIRED"  -> handleExpired(payment, log);
            case "PAYMENT_FAILED"   -> handleFailed(payment, log);
            default -> log.markSkipped("지원하지 않는 eventType: " + eventType);
        }
    }

    private void handleApproved(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.DONE);
        log.markProcessed("결제 승인 상태 동기화 완료");
    }

    private void handleCanceled(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.CANCELED);
        log.markProcessed("결제 취소 상태 동기화 완료");
    }

    private void handleExpired(Payment payment, WebhookLog log) {
        // PaymentStatus에 EXPIRED 없으면 추가해줘야 함
        payment.updateStatus(PaymentStatus.EXPIRED);
        log.markProcessed("결제 만료 상태 동기화 완료");
    }

    private void handleFailed(Payment payment, WebhookLog log) {
        payment.updateStatus(PaymentStatus.FAILED);
        log.markProcessed("결제 실패 상태 동기화 완료");
    }
}
