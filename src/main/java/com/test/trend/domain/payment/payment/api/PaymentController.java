package com.test.trend.domain.payment.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 결제(Payment) 관련 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/trend/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 처리 API")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 내부적으로 결제 요청을 PENDING 상태로 기록한다.
     */
    @PostMapping("/record")
    @Operation(summary = "결제 요청 기록", description = "결제 요청 정보를 PENDING 상태로 저장한다.")
    public ResponseEntity<PaymentDTO> record(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.recordPaymentRequest(dto));
    }

    /**
     * Toss 결제 승인(confirm)을 처리한다.
     */
    @PostMapping("/confirm")
    @Operation(summary = "Toss 결제 승인 처리", description = "Toss Payments 결제 승인 API를 호출하고 결과를 저장한다.")
    public ResponseEntity<PaymentDTO> confirm(@RequestBody TossPaymentConfirmRequest request) {
        return ResponseEntity.ok(paymentService.confirmTossPayment(request));
    }
}
