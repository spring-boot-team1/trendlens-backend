package com.test.trend.domain.payment.subscription.mapper;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;

public interface UserSubscriptionMapper {

    UserSubscriptionDTO toDto(UserSubscription entity);

    /**
     * 연관관계(SubscriptionPlan)는 Service에서 주입
     */
    UserSubscription toEntity(UserSubscriptionDTO dto);
}
