package com.test.trend.domain.payment.subscription.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.mapper.UserSubscriptionMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;
import com.test.trend.enums.SubscriptionStatus;
import com.test.trend.enums.YesNo;

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
		planRepository.findById(dto.getSeqSubscriptionPlan())
				.orElseThrow(() -> new IllegalArgumentException("Subscription plan not found"));
		
		// 기본 설정
        LocalDateTime now = LocalDateTime.now();
		
		UserSubscription entity = UserSubscription.builder()
				.seqAccount(dto.getSeqAccount())
				.seqSubscriptionPlan(dto.getSeqSubscriptionPlan())
				.startDate(now)
                .endDate(now.plusMonths(1))
                .nextBillingDate(now.plusMonths(1))
                .autoRenewYn(YesNo.Y)
                .status(SubscriptionStatus.ACTIVE)
                .createdAt(now)
				.build();
		
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
		
		return mapper.toDto(sub);
	}
	
	 /**
     * 결제 승인 시 구독 갱신 또는 생성
     */
	public void processPayment(Payment payment) {

	    Long seqAccount = payment.getSeqAccount();

	    // ACTIVE 구독 찾기
	    UserSubscription subscription = userRepository
		        .findActiveBySeqAccount(seqAccount)
		        .orElseGet(() -> createNewSubscription(seqAccount));

	    // 다음 결제일 +1개월
	    subscription.extendBillingDate();
	}
	
	/** 활성 구독이 없을 경우 새로 생성 */
    private UserSubscription createNewSubscription(Long seqAccount) {

        LocalDateTime now = LocalDateTime.now();

        UserSubscription sub = UserSubscription.builder()
                .seqAccount(seqAccount)
                .seqSubscriptionPlan(1L) // 기본 플랜? 필요 시 외부 파라미터
                .startDate(now)
                .endDate(now.plusMonths(1))
                .nextBillingDate(now.plusMonths(1))
                .autoRenewYn(YesNo.Y)
                .status(SubscriptionStatus.ACTIVE)
                .createdAt(now)
                .build();

        return userRepository.save(sub);
    }
	
}


