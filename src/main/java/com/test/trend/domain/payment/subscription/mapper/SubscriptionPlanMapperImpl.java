package com.test.trend.domain.payment.subscription.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;

@Component
public class SubscriptionPlanMapperImpl implements SubscriptionPlanMapper {

    @Override
    public SubscriptionPlanDTO toDto(SubscriptionPlan entity) {
        if (entity == null) {
            return null;
        }

        return SubscriptionPlanDTO.builder()
                .seqSubscriptionPlan(entity.getSeqSubscriptionPlan())

                .planName(entity.getPlanName())
                .planDescription(entity.getPlanDescription())

                .monthlyFee(entity.getMonthlyFee())
                .durationMonth(entity.getDurationMonth())
                .status(entity.getStatus())

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

	@Override
	public SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}
}
