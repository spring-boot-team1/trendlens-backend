package com.test.trend.domain.payment.trend.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.trend.dto.TrendSnapshotDTO;
import com.test.trend.domain.payment.trend.entity.TrendSnapshot;

@Component
public class TrendSnapshotMapper {

	public TrendSnapshotDTO toDto(TrendSnapshot entity) {
		return TrendSnapshotDTO.builder()
				.seqTrendSnapshot(entity.getSeqTrendSnapshot())
				.snapshotDate(entity.getSnapshotDate())
	            .topBrand(entity.getTopBrand())
	            .topCategory(entity.getTopCategory())
	            .styleTrend(entity.getStyleTrend())
	            .createdAt(entity.getCreatedAt())
				.build();
	}
	
	public TrendSnapshot toEntity(TrendSnapshotDTO dto) {
		return TrendSnapshot.builder()
				.seqTrendSnapshot(dto.getSeqTrendSnapshot())
				.snapshotDate(dto.getSnapshotDate())
	            .topBrand(dto.getTopBrand())
	            .topCategory(dto.getTopCategory())
	            .styleTrend(dto.getStyleTrend())
	            .createdAt(dto.getCreatedAt())
				.build();
	}
	
}
