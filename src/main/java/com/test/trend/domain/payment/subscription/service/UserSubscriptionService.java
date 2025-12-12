package com.test.trend.domain.payment.subscription.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.dto.UserSubscriptionStatusResponse;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;
import com.test.trend.domain.payment.subscription.mapper.UserSubscriptionMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;
import com.test.trend.domain.payment.subscription.repository.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 구독(UserSubscription)을 관리하는 서비스.
 * <p>
 * - 신규 구독 생성<br>
 * - 결제 성공 시 기간 연장<br>
 * - 구독 취소/만료 처리<br>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionService {

    private final UserSubscriptionRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionMapper mapper;
    
    @Transactional(readOnly = true)
    public UserSubscriptionStatusResponse getSubscriptionStatus(Long seqAccount) {

        UserSubscription sub = userRepository.findActiveBySeqAccount(seqAccount)
            .orElseThrow(() -> new IllegalStateException("활성 구독이 없습니다."));

        SubscriptionPlan plan = sub.getSeqSubscriptionPlan();

        return UserSubscriptionStatusResponse.builder()
            .planName(plan.getPlanName())
            .status(sub.getStatus().name())
            .startDate(sub.getStartDate())
            .nextBillingDate(sub.getNextBillingDate())
            .build();
    }

    /**
     * 신규 사용자 구독을 생성한다.
     * <p>
     * - seqAccount, seqSubscriptionPlan 값을 기반으로 SubscriptionPlan을 조회<br>
     * - UserSubscription.create(seqAccount, plan)을 사용하여 엔티티 생성<br>
     * </p>
     *
     * @param dto 구독 생성에 필요한 정보
     * @return 생성된 사용자 구독 정보
     */
    public UserSubscriptionDTO startSubscription(UserSubscriptionDTO dto) {

        SubscriptionPlan plan = planRepository.findById(dto.getSeqSubscriptionPlan())
                .orElseThrow(() -> new IllegalArgumentException("Subscription plan not found"));

        UserSubscription entity = UserSubscription.create(dto.getSeqAccount(), plan);
        UserSubscription saved = userRepository.save(entity);

        return mapper.toDto(saved);
    }

    /**
     * 사용자 구독을 단건 조회한다.
     *
     * @param seqUserSub 사용자 구독 PK
     * @return 사용자 구독 정보 (없으면 null)
     */
    @Transactional(readOnly = true)
    public UserSubscriptionDTO findById(Long seqUserSub) {
        return userRepository.findById(seqUserSub)
                .map(mapper::toDto)
                .orElse(null);
    }

    /**
     * 사용자 구독을 취소한다.
     * <p>
     * - 상태를 CANCELED로 변경<br>
     * - autoRenewYn 을 N 으로 변경<br>
     * </p>
     *
     * @param seqUserSub 사용자 구독 PK
     * @param reason     취소 사유
     * @return 취소된 사용자 구독 정보
     */
    public UserSubscriptionDTO cancelSubscription(Long seqUserSub, String reason) {
        UserSubscription sub = userRepository.findById(seqUserSub)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        sub.cancel(reason);
        return mapper.toDto(sub);
    }

    /**
     * 결제 승인(Payment) 시 구독을 갱신하거나 존재하지 않으면 새로 생성한다.
     * <p>
     * - seqAccount 기준 ACTIVE 구독 검색<br>
     * - 없으면 기본 플랜으로 신규 구독 생성<br>
     * - SubscriptionPlan의 durationMonth 만큼 기간 연장<br>
     * </p>
     *
     * @param payment 승인된 결제 정보
     */
    public UserSubscription processPayment(Payment payment) {

        Long seqAccount = payment.getSeqAccount();

        // ACTIVE 구독 조회 (없으면 새로 생성)
        UserSubscription subscription =
        		userRepository.findActiveBySeqAccount(seqAccount)
                    .orElseGet(() -> createNewSubscription(seqAccount));

        // 현재 구독에 연결된 플랜 기준으로 기간 연장
        SubscriptionPlan plan = subscription.getSeqSubscriptionPlan();
        int months = plan.getDurationMonth();
        subscription.extendBillingDate(months);
        
        return subscription;
    }

    /**
     * 활성 구독이 없는 경우 기본 플랜으로 신규 구독을 생성한다.
     *
     * @param seqAccount 사용자 계정 PK
     * @return 생성된 UserSubscription 엔티티
     */
    private UserSubscription createNewSubscription(Long seqAccount) {

        // TODO: 실제 구현 시 "기본 플랜" 전략 필요 (지금은 1L 고정)
        SubscriptionPlan defaultPlan = planRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("기본 구독 플랜이 존재하지 않습니다."));

        UserSubscription sub = UserSubscription.create(seqAccount, defaultPlan);
        return userRepository.save(sub);
    }

    /**
     * 정기 결제 실패, 만료 등으로 인해 구독을 만료 상태로 변경한다.
     *
     * @param seqUserSub 사용자 구독 PK
     * @return 만료 처리된 구독 정보
     */
    public UserSubscriptionDTO expireSubscription(Long seqUserSub) {
        UserSubscription sub = userRepository.findById(seqUserSub)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        sub.expire();
        return mapper.toDto(sub);
    }
}
