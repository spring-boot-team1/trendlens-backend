package com.test.trend.domain.payment.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.payment.entity.WebhookLog;

public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {

    Optional<WebhookLog> findTopByPaymentKeyAndEventTypeOrderByReceivedAtDesc(String paymentKey, String eventType);
}
