package com.test.trend.domain.payment.subscription.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.entity.UserSubscription;

@Component
public class UserSubscriptionMapper {

	public UserSubscriptionDTO toDto(UserSubscription entity) {
		return UserSubscriptionDTO.builder()
				.seqUserSub(entity.getSeqUserSub())
				.seqAccount(entity.getSeqAccount())
				.seqSubscriptionPlan(entity.getSeqSubscriptionPlan())
	            .startDate(entity.getStartDate())
	            .endDate(entity.getEndDate())
	            .nextBillingDate(entity.getNextBillingDate())
	            .autoRenewYn(entity.getAutoRenewYn())
	            .status(entity.getStatus())
	            .cancelReason(entity.getCancelReason())
	            .createdAt(entity.getCreatedAt())
				.build();
	}
	
	public UserSubscription toEntity(UserSubscriptionDTO dto) {
		return UserSubscription.builder()
				.seqUserSub(dto.getSeqUserSub())
				.seqAccount(dto.getSeqAccount())
	            .seqSubscriptionPlan(dto.getSeqSubscriptionPlan())
	            .startDate(dto.getStartDate())
	            .endDate(dto.getEndDate())
	            .nextBillingDate(dto.getNextBillingDate())
	            .autoRenewYn(dto.getAutoRenewYn())
	            .status(dto.getStatus())
	            .cancelReason(dto.getCancelReason())
	            .createdAt(dto.getCreatedAt())
				.build();
	}
	
}
