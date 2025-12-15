package com.test.trend.domain.payment.subscription.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.service.SubscriptionPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 구독 상품(SubscriptionPlan)에 대한 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/v1/subscriptions/plans")
@RequiredArgsConstructor
@Tag(name = "SubscriptionPlan", description = "구독 상품 관리 API")
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    /**
     * 새로운 구독 상품을 생성한다.
     */
    @PostMapping
    @Operation(summary = "구독 상품 생성", description = "새로운 구독 상품을 생성한다.")
    public ResponseEntity<SubscriptionPlanDTO> create(@RequestBody SubscriptionPlanDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /**
     * 모든 구독 상품 목록을 조회한다.
     */
    @GetMapping
    @Operation(summary = "구독 상품 전체 조회", description = "등록된 모든 구독 상품 목록을 조회한다.")
    public ResponseEntity<List<SubscriptionPlanDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * 단일 구독 상품을 조회한다.
     */
    @GetMapping("/{seqSubscriptionPlan}")
    @Operation(summary = "구독 상품 단건 조회", description = "PK 기준으로 단일 구독 상품을 조회한다.")
    public ResponseEntity<SubscriptionPlanDTO> findById(@PathVariable Long seqSubscriptionPlan) {
        SubscriptionPlanDTO dto = service.findById(seqSubscriptionPlan);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    /**
     * 구독 상품의 상태(status)를 변경한다.
     */
    @PatchMapping("/{seqSubscriptionPlan}/status")
    @Operation(summary = "구독 상품 상태 변경", description = "구독 상품의 상태(status)를 변경한다.")
    public ResponseEntity<SubscriptionPlanDTO> updateStatus(
            @PathVariable Long seqSubscriptionPlan,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(service.updateStatus(seqSubscriptionPlan, status));
    }
}
