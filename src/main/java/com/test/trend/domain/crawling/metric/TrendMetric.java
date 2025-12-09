package com.test.trend.domain.crawling.metric;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.YesNo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.*;

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
@AllArgsConstructor
@Builder
public class TrendMetric {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTrendMetricGenerator")
	private Long seqTrendMetric;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	private LocalDate baseDate;
	private Double ratio;
	
	@Enumerated(EnumType.STRING)
	private YesNo isHot = YesNo.N;
	
	private String rawJson;
	
	private LocalDateTime createdAt;


}
