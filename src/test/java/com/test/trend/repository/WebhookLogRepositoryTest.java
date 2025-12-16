package com.test.trend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.test.trend.domain.payment.payment.entity.WebhookLog;
import com.test.trend.domain.payment.payment.repository.WebhookLogRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WebhookLogRepositoryTest {

    @Autowired
    WebhookLogRepository repo;

    @Test
    void 웹훅로그_저장성공() {
        WebhookLog log = WebhookLog.builder()
                .eventType("PAYMENT_APPROVED")
                .paymentKey("pay123")
                .payload("{}")
                .signature("sig")
                .receivedAt(LocalDateTime.now())
                .build();

        WebhookLog saved = repo.save(log);

        assertThat(saved.getPaymentKey()).isEqualTo("pay123");
    }
}

