package com.test.trend.domain.payment.payment.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TossWebhookSignatureValidator {

    @Value("${toss.webhook.secret}")
    private String webhookSecret;

    /**
     * Toss 문서 규격에 따라 실제 서명 검증 로직을 맞춰야 한다.
     * 여기서는 "payload에 대해 HMAC-SHA256 → Base64" 예시 코드.
     */
    public boolean isValid(String signature, String payload) {
        try {
            String expected = generateSignature(payload);
            // 실제 운영에서는 time-constant 비교 권장
            return timingSafeEquals(expected, signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateSignature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }

    private boolean timingSafeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
