package com.test.trend.domain.payment.subscription.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.service.SubscriptionPlanService;

import lombok.RequiredArgsConstructor;

/**
 * 구독 상품(SubscriptionPlan)에 대한 REST API 컨트롤러.
 * <p>
 * 구독 상품의 생성, 단건 조회, 전체 조회, 상태 변경 기능을 제공한다.
 * 비즈니스 로직과 DTO ↔ Entity 변환은 Service 계층에서 처리되며,
 * 이 컨트롤러는 요청/응답만 담당한다.
 */
@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

	private final SubscriptionPlanService service;
	
	/**
	 * 새로운 구독 상품을 생성한다.
	 * 
	 * @param dto 생성할 구독 상품 정보 DTO
	 * @return 생성된 구독 상품 DTO
	 */
	@PostMapping
	public SubscriptionPlanDTO create(@RequestBody SubscriptionPlanDTO dto) {
		return service.create(dto);
	}
	
	/**
	 * 구독 상품을 ID 기준으로 조회한다.
	 * 
	 * @param seqSubscriptionPlan 구독 상품 PK 값
	 * @return 조회된 구독 상품 DTO, 존재하지 않으면 null 반환
	 */
	@GetMapping("/{seqSubscriptionPlan}")
	public SubscriptionPlanDTO findById(@PathVariable("seqSubscriptionPlan") Long seqSubscriptionPlan) {
		return service.findById(seqSubscriptionPlan);
	}
	
	/**
	 * 전체 구독 상품 목록을 조회한다.
	 * 
	 * @return 구독 상품 DTO 리스트
	 */
	@GetMapping
	public List<SubscriptionPlanDTO> findAll() {
		return service.findAll();
	}
	
	/**
	 * 특정 구독 상품의 상태(status)를 변경한다.
	 * 
	 * @param seqSubscriptionPlan 상태를 변경할 구독 상품 PK
	 * @param status 변경할 상태 문자열
	 * @return 상태 변경 후의 구독 상품 DTO
	 */
	@PutMapping("/{seqSubscriptionPlan}/status")
	public SubscriptionPlanDTO updateStatus(
			@PathVariable("seqSubscriptionPlan") Long seqSubscriptionPlan,
			@RequestParam("status") String status
	) {
		return service.updateStatus(seqSubscriptionPlan, status);
	}
	
}


