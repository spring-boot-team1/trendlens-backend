package com.test.trend.domain.payment.payment.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.payment.dto.toss.TossWebhookRequest;
import com.test.trend.domain.payment.payment.service.TossWebhookSignatureValidator;
import com.test.trend.domain.payment.payment.service.WebhookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class WebhookController {

	private final WebhookService service;
	private final TossWebhookSignatureValidator validator;
    private final ObjectMapper mapper;
	
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(name = "Toss-Signature", required = false) String signature,
            @RequestBody String body) {

        // 1) 서명 헤더 없으면 거절
        if (signature == null || signature.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MISSING_SIGNATURE");
        }

        // 2) 서명 검증 실패
        if (!signatureValidator.isValid(signature, body)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_SIGNATURE");
        }

        // 3) JSON 파싱
        TossWebhookRequest request;
        try {
            request = objectMapper.readValue(body, TossWebhookRequest.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_PAYLOAD");
        }

        // 4) 서비스 처리 (로그 저장 + 중복 체크 + Payment 업데이트)
        webhookService.processWebhook(request, body, signature);

        // Toss는 200 OK를 받으면 성공으로 간주하고 재전송을 멈춤
        return ResponseEntity.ok("OK");
    }
}
