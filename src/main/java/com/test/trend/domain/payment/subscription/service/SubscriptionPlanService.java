package com.test.trend.domain.payment.subscription.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.mapper.SubscriptionPlanMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

/**
 * 구독 상품(SubscriptionPlan)을 관리하는 서비스 클래스
 * CRUD 및 상태 변경 기능을 제공
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionPlanService {
	
	private final SubscriptionPlanRepository repository;
	private final SubscriptionPlanMapper mapper;
	
	/**
	 * 구독 상품 생성
	 * 
	 * @param dto 생성할 구독 상품 DTO
	 * @return 생성된 구독 상품 DTO
	 */
	public SubscriptionPlanDTO create(SubscriptionPlanDTO dto) {
		SubscriptionPlan entity = mapper.toEntity(dto);
		SubscriptionPlan saved = repository.save(entity);
		return mapper.toDto(saved);
	}
	
	/**
	 * 구독 상품을 PK(seqSubscriptionPlan)로 조회
	 * @param seqSubscriptionPlan 구독 상품 PK
	 * @return 조회된 구독 상품 DTO (없으면 null)
	 */
	public SubscriptionPlanDTO findById(Long seqSubscriptionPlan) {
		return repository.findById(seqSubscriptionPlan)
				.map(mapper::toDto)
				.orElse(null);
	}
	
	/**
	 * 모든 구독 상품 목록을 조회
	 * @return 구독 상품 DTO 리스트
	 */
	public List<SubscriptionPlanDTO> findAll() {
		return repository.findAll().stream()
				.map(mapper::toDto)
				.toList();
	}
	
	/**
	 * 구독 상품의 상태를 변경
	 * @param seqSubscriptionPlan 구독 상품 PK
	 * @param status 변경할 상태
	 * @return 상태가 변경된 구독 상품 DTO
	 */
	public SubscriptionPlanDTO updateStatus(Long seqSubscriptionPlan, String status) {
		SubscriptionPlan plan = repository.findById(seqSubscriptionPlan)
				.orElseThrow(() -> new IllegalArgumentException("Plan not found"));
		
		plan.updateStatus(status);
		return mapper.toDto(plan);
				
	}

}





