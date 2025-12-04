package com.test.trend.domain.payment.subscription.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.mapper.UserSubscriptionMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 구독(UserSubscription) 정보를 처리하는 서비스
 * 구독 시작, 갱신, 취소 등의 로직을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionService {

	private final UserSubscriptionRepository userRepository;
	private final SubscriptionPlanRepository planRepository;
	private final UserSubscriptionMapper mapper;
	
	/**
	 * 새로운 사용자 구독 시작
	 * 
	 * @param dto 구독 정보 DTO
	 * @return 생성된 사용자 구독 DTO
	 */
	public UserSubscriptionDTO startSubscription(UserSubscriptionDTO dto) {
		
		//구독 플랜 유효성 체크
		planRepository.findById(dto.getSeqSubscriptionPlan()).orElseThrow(() -> new IllegalArgumentException("Subscription plan not found"));
		
		UserSubscription entity = mapper.toEntity(dto);
		UserSubscription saved = userRepository.save(entity); 
		
		return mapper.toDto(saved);
	}
	
	/**
	 * 사용자 구독 취소
	 * status 값을 ('ACTIVE' → 'CANCELLED') 상태로 수정
	 * @param seqUserSub 사용자 구독 PK
	 * @param reason 취소 사유
	 * @return 취소된 사용자 구독 DTO
	 */
	public UserSubscriptionDTO cancelSubscription(Long seqUserSub, String reason) {
		UserSubscription sub = userRepository.findById(seqUserSub)
				.orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

		UserSubscription updated = UserSubscription.builder()
				.seqUserSub(sub.getSeqUserSub())
				.seqAccount(sub.getSeqAccount())
                .seqSubscriptionPlan(sub.getSeqSubscriptionPlan())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .nextBillingDate(sub.getNextBillingDate())
                .autoRenewYn("N")
                .status("CANCELLED")
                .cancelReason(reason)
                .createdAt(sub.getCreatedAt())
				.build();
		
		userRepository.save(updated);
		return mapper.toDto(updated);
	}
	
}


