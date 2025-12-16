package com.test.trend.domain.payment.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.payment.dto.PaymentConfirmResponse;
import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.mapper.PaymentMapper;
import com.test.trend.domain.payment.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 결제(Payment) 관련 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 처리 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper mapper;

    /**
     * 결제 요청을 PENDING 상태로 기록한다.
     * (결제 버튼 클릭 시 내부적으로 호출 가능)
     */
    //@Operation(summary = "결제 요청 기록", description = "결제 요청 정보를 PENDING 상태로 저장한다.")
    @PostMapping("/record")
    public ResponseEntity<Void> record(@RequestBody PaymentDTO dto) {

        paymentService.recordPaymentRequest(
            dto.getSeqAccount(),
            dto.getOrderId(),
            dto.getAmount(),
            dto.getSeqSubscriptionPlan()
        );

        return ResponseEntity.ok().build();
    }

    /**
     * Toss 결제 승인(confirm) + 구독 생성/연장 처리
     */
    @PostMapping("/confirm")
    @Operation(
        summary = "Toss 결제 승인 처리",
        description = "Toss Payments 결제 승인 후 결제 상태를 저장하고 구독을 생성/갱신한다."
    )
    public ResponseEntity<PaymentConfirmResponse> confirm(
            @RequestBody TossPaymentConfirmRequest request
    ) {
        return ResponseEntity.ok(
            paymentService.confirmTossPaymentAndSubscribe(request)
        );
    }
}
