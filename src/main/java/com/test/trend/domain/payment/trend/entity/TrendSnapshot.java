package com.test.trend.domain.payment.trend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendSnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTrendSnapshotGenerator")
	@SequenceGenerator(name = "seqTrendSnapshotGenerator", sequenceName ="seqTrendSnapshot", allocationSize = 1)
	private Long seqTrendSnapshot;
	
	private LocalDateTime snapshotDate;
	
	@Lob
	private String topBrand;
	
	@Lob
	private String topCategory;
	
	@Lob
	private String styleTrend;
	private LocalDateTime createdAt;
	
}
