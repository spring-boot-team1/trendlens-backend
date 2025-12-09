package com.test.trend.domain.payment.payment.dto.toss;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossWebhookRequest {

	private String eventType; // PAYMENT_APPROVED, PAYMENT_CANCELED 등
    private TossWebhookData data;

    @Getter
    @Setter
    public static class TossWebhookData {
        private String paymentKey;
        private String orderId;
        private String status;  // DONE, CANCELED
        private Long totalAmount;
    }
    
}
