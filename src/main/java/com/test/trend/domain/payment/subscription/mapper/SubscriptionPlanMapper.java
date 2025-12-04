package com.test.trend.domain.payment.subscription.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;

@Component
public class SubscriptionPlanMapper {

	public SubscriptionPlanDTO toDto(SubscriptionPlan entity) {
		if (entity == null) return null;
		
		return SubscriptionPlanDTO.builder()
				.seqSubscriptionPlan(entity.getSeqSubscriptionPlan())
				.seqAccount(entity.getSeqAccount())
				.planName(entity.getPlanName())
                .planDescription(entity.getPlanDescription())
                .monthlyFee(entity.getMonthlyFee())
                .durationMonth(entity.getDurationMonth())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updateAt(entity.getUpdateAt())
				.build();
	}
	
	public SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
		if (dto == null) return null;
		
		return SubscriptionPlan.builder()
				.seqSubscriptionPlan(dto.getSeqSubscriptionPlan())
				.seqAccount(dto.getSeqAccount())
				.planName(dto.getPlanName())
                .planDescription(dto.getPlanDescription())
                .monthlyFee(dto.getMonthlyFee())
                .durationMonth(dto.getDurationMonth())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updateAt(dto.getUpdateAt())
				.build();
	}
	
}
