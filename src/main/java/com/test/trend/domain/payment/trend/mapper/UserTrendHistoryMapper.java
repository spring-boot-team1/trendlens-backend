package com.test.trend.domain.payment.trend.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.trend.dto.UserTrendHistoryDTO;
import com.test.trend.domain.payment.trend.entity.UserTrendHistory;

@Component
public class UserTrendHistoryMapper {

	public UserTrendHistoryDTO toDto(UserTrendHistory entity) {
		return UserTrendHistoryDTO.builder()
				.seqUserTrendHistory(entity.getSeqUserTrendHistory())
				.seqAccount(entity.getSeqAccount())
	            .seqKeyword(entity.getSeqKeyword())
	            .viewAt(entity.getViewAt())
	            .sourcePage(entity.getSourcePage())
				.build();
	}
	
	public UserTrendHistory toEntity(UserTrendHistoryDTO dto) {
		return UserTrendHistory.builder()
				.seqUserTrendHistory(dto.getSeqUserTrendHistory())
				.seqAccount(dto.getSeqAccount())
	            .seqKeyword(dto.getSeqKeyword())
	            .viewAt(dto.getViewAt())
	            .sourcePage(dto.getSourcePage())
				.build();
	}
	
}
