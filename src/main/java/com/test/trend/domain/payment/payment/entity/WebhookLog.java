package com.test.trend.domain.payment.payment.entity;

import java.time.LocalDateTime;

import com.test.trend.enums.YesNo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "WebhookLog")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WebhookLog {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqWebhookLogGenerator")
    @SequenceGenerator(name = "seqWebhookLogGenerator", sequenceName = "seqWebhookLog", allocationSize = 1)
    private Long seqWebhookLog;

    private String eventType;     // PAYMENT_APPROVED 등
    private String paymentKey;    // Toss paymentKey
    private String signature;     // 헤더로 받은 서명값
    private String payload;       // Raw JSON

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YesNo processed;    // 비즈니스 로직 처리 여부
    private String note;          // 실패 이유, 스킵 사유 등

    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;

    public void markProcessed(String note) {
        this.processed = YesNo.Y;
        this.note = note;
        this.processedAt = LocalDateTime.now();
    }

    public void markSkipped(String note) {
        this.processed = YesNo.N;
        this.note = note;
        this.processedAt = LocalDateTime.now();
    }

}
