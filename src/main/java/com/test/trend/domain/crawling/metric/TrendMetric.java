package com.test.trend.domain.crawling.metric;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TrendMetric")
@SequenceGenerator(
		name = "seqTrendMetricGenerator",
		sequenceName = "seqTrendMetric",
		allocationSize = 1
		)
@Getter
@Setter
@NoArgsConstructor
public class TrendMetric {
	
	private Long seqTrendMetric;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	private LocalDateTime baseDate;
	private Double ratio;
	private String isHot = "N";
	private String rawJson;
	
	private LocalDateTime createdAt;
	

}
