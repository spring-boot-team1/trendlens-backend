package com.test.trend.domain.payment.subscription.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 구독(UserSubscription)에 대한 REST API 컨트롤러.
 * 구독 시작(start), 구독 취소(cancel) 기능만 제공한다.
 * 비즈니스 로직 및 DTO ↔ Entity 변환은 Service 계층에서 수행된다.
 */
@RestController
@RequestMapping("/api/v1/user-subscription")
@RequiredArgsConstructor
public class UserSubscriptionController {

	private final UserSubscriptionService service;
	
	/**
	 * 새로운 사용자 구독을 시작한다.
	 * <p>
	 * DTO에 포함된 seqSubscriptionPlan 값을 기반으로 유효한 플랜인지 검증하고,
	 * 구독 정보를 저장한 후 저장된 사용자 구독 DTO를 반환한다.
	 * 
	 * @param dto 사용자 구독 정보가 담긴 DTO
	 * @return 생성된 사용자 구독 DTO
	 */
	@PostMapping
	public UserSubscriptionDTO startSubscription(@RequestBody UserSubscriptionDTO dto) {
		return service.startSubscription(dto);
	}
	
	/**
	 * 특정 사용자 구독을 취소한다.
	 * <p>
	 * 구독 상태(status)를 "CANCELED"로 변경하며, 취소 사유(cancelReason)를 기록한다.
	 * DTO는 Service 내부에서 다시 빌더 패턴으로 조립되므로 Controller에서는 PK와 reason만 전달한다.
	 * 
	 * @param seqUserSub
	 * @param season
	 * @return
	 */
	@PutMapping("/{seqUserSub}/cancel")
	public UserSubscriptionDTO cancelSubscription(
			@PathVariable Long seqUserSub,
			@RequestParam String season
	) {
		return service.cancelSubscription(seqUserSub, season);
	}
	
}
