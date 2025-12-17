package com.test.trend.domain.payment.subscription.mapper;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;

public interface SubscriptionPlanMapper {

    SubscriptionPlanDTO toDto(SubscriptionPlan entity);

    SubscriptionPlan toEntity(SubscriptionPlanDTO dto);
}
