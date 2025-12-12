package com.test.trend.domain.payment.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;


@Mapper(componentModel = "spring")
@Component
public interface SubscriptionPlanMapper {

	SubscriptionPlanDTO toDto(SubscriptionPlan entity);

	@Mapping(target = "seqSubscriptionPlan", ignore = true) // 생성 시 자동 생성
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionPlan toEntity(SubscriptionPlanDTO dto);
}
