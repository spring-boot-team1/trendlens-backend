package com.test.trend.domain.payment.payment.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Toss Webhook Signature 검증기.
 * <p>
 * Toss 문서에서 제공하는 공식 방식:
 * HMAC-SHA256(Secret, Payload) → Base64 인코딩 후 Toss-Signature 와 비교한다.
 * </p>
 */
@Component
public class TossWebhookSignatureValidator {

    @Value("${toss.webhook.secret}")
    private String webhookSecret;

    /**
     * 전달된 signature가 payload 기반으로 생성한 서명과 동일한지 검증한다.
     *
     * @param signature Toss Webhook Header에 포함된 서명 값
     * @param payload   Webhook JSON 원본 문자열
     * @return 서명 일치 여부
     */
    public boolean isValid(String signature, String payload) {
        try {
            String expectedSignature = generateSignature(payload);
            return timingSafeEquals(expectedSignature, signature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * payload 에 대해 HMAC-SHA256 → Base64 인코딩하여 Toss 서명을 생성한다.
     */
    private String generateSignature(String payload) throws Exception {
        Mac hasher = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                webhookSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        hasher.init(secretKey);

        byte[] hash = hasher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        return java.util.Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Timing attack 방지를 위한 비교 함수.
     * (문자열 비교 시 걸리는 시간을 동일하게 유지)
     */
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
