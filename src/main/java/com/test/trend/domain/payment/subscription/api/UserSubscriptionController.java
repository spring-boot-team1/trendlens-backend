package com.test.trend.domain.payment.subscription.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 구독(UserSubscription)에 대한 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/trend/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "UserSubscription", description = "사용자 구독 관리 API")
public class UserSubscriptionController {

    private final UserSubscriptionService service;

    /**
     * 신규 구독을 생성한다.
     */
    @PostMapping
    @Operation(summary = "구독 시작", description = "사용자의 신규 구독을 생성한다.")
    public ResponseEntity<UserSubscriptionDTO> start(@RequestBody UserSubscriptionDTO dto) {
        return ResponseEntity.ok(service.startSubscription(dto));
    }

    /**
     * 구독 단건을 조회한다.
     */
    @GetMapping("/{seqUserSub}")
    @Operation(summary = "구독 단건 조회", description = "PK 기준으로 사용자 구독 정보를 조회한다.")
    public ResponseEntity<UserSubscriptionDTO> findById(@PathVariable Long seqUserSub) {
        UserSubscriptionDTO dto = service.findById(seqUserSub);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    /**
     * 구독을 취소한다.
     */
    @PostMapping("/{seqUserSub}/cancel")
    @Operation(summary = "구독 취소", description = "사용자 구독을 취소 처리한다.")
    public ResponseEntity<UserSubscriptionDTO> cancel(
            @PathVariable Long seqUserSub,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(service.cancelSubscription(seqUserSub, reason));
    }
}
