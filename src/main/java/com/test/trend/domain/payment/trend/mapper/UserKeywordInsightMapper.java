package com.test.trend.domain.payment.trend.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.trend.dto.UserKeywordInsightDTO;
import com.test.trend.domain.payment.trend.entity.UserKeywordInsight;

@Component
public class UserKeywordInsightMapper {

	public UserKeywordInsightDTO toDto(UserKeywordInsight entity) {
		return UserKeywordInsightDTO.builder()
				.seqUserKeywordInsight(entity.getSeqUserKeywordInsight())
				.seqAccount(entity.getSeqAccount())
	            .seqKeyword(entity.getSeqKeyword())
	            .insightText(entity.getInsightText())
	            .trendScore(entity.getTrendScore())
	            .hotYn(entity.getHotYn())
	            .createdAt(entity.getCreatedAt())
				.build();
	}
	
	public UserKeywordInsight toEntity(UserKeywordInsightDTO dto) {
		return UserKeywordInsight.builder()
				.seqUserKeywordInsight(dto.getSeqUserKeywordInsight())
				.seqAccount(dto.getSeqAccount())
	            .seqKeyword(dto.getSeqKeyword())
	            .insightText(dto.getInsightText())
	            .trendScore(dto.getTrendScore())
	            .hotYn(dto.getHotYn())
	            .createdAt(dto.getCreatedAt())
				.build();
	}
	
}
