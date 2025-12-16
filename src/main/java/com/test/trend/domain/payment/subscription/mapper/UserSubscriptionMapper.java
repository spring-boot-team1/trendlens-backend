package com.test.trend.domain.payment.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;

@Mapper(componentModel = "spring")
public interface UserSubscriptionMapper {

	@Mapping(source = "subscriptionPlan.seqSubscriptionPlan", target = "seqSubscriptionPlan")
    @Mapping(source = "subscriptionPlan.planName", target = "planName")
    UserSubscriptionDTO toDto(UserSubscription entity);

    /** Entity 생성 시 DTO → Entity 직접 매핑할 일 거의 없음 → ignore 처리 */
    @Mapping(target = "subscriptionPlan", ignore = true)
    @Mapping(target = "seqUserSub", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserSubscription toEntity(UserSubscriptionDTO dto);
    
}
