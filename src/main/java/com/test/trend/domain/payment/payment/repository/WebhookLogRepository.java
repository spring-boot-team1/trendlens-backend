package com.test.trend.domain.payment.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.payment.entity.WebhookLog;

public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {

	// 동일 paymentKey + eventType 조합이 이미 처리된 적 있는지 체크
    boolean existsByPaymentKeyAndEventTypeAndProcessedTrue(String paymentKey, String eventType);

    Optional<WebhookLog> findTopByPaymentKeyAndEventTypeOrderByReceivedAtDesc(String paymentKey, String eventType);
}
