package com.test.trend.domain.payment.subscription.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;

@Component
public class UserSubscriptionMapperImpl implements UserSubscriptionMapper {

    @Override
    public UserSubscriptionDTO toDto(UserSubscription entity) {
        if (entity == null) return null;

        SubscriptionPlan plan = entity.getSeqSubscriptionPlan();

        return UserSubscriptionDTO.builder()
                .seqUserSub(entity.getSeqUserSub())
                .seqAccount(entity.getSeqAccount())

                // 연관 엔티티 안전 처리
                .seqSubscriptionPlan(
                        plan != null ? plan.getSeqSubscriptionPlan() : null
                )
                .planName(
                        plan != null ? plan.getPlanName() : null
                )

                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .nextBillingDate(entity.getNextBillingDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public UserSubscription toEntity(UserSubscriptionDTO dto) {
        if (dto == null) return null;

        return UserSubscription.builder()
                .seqAccount(dto.getSeqAccount())
                .status(dto.getStatus())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .nextBillingDate(dto.getNextBillingDate())
                .build();
    }
}
