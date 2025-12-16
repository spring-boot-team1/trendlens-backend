package com.test.trend.domain.payment.payment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookLogDTO {
	
	private Long seqWebhookLog;
    private String eventType;
    private String paymentKey;
    private String processed;
    private LocalDateTime receivedAt;
    
}
