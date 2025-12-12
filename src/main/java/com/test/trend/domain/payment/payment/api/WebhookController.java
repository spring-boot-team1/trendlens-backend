package com.test.trend.domain.payment.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.trend.domain.payment.payment.service.TossWebhookSignatureValidator;
import com.test.trend.domain.payment.payment.service.WebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Toss Webhook 수신용 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/trend/api/v1/payments/webhook")
@RequiredArgsConstructor
@Tag(name = "Webhook", description = "Toss Webhook 수신 API")
public class WebhookController {

    private final WebhookService webhookService;
    private final TossWebhookSignatureValidator signatureValidator;

    /**
     * Toss Webhook을 수신한다.
     * <p>
     * - 시그니처 검증 후 유효한 경우에만 WebhookService 로 위임<br>
     * </p>
     */
    @PostMapping
    @Operation(summary = "Toss Webhook 처리", description = "Toss에서 전송하는 Webhook 이벤트를 수신하여 처리한다.")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("Toss-Signature") String signature,
            @RequestBody String payload
    ) {

        boolean valid = signatureValidator.isValid(signature, payload);
        if (!valid) {
            return ResponseEntity.status(400).build(); // 시그니처 검증 실패
        }

        webhookService.processWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
