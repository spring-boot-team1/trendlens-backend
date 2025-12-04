package com.test.trend.domain.crawling.score;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TrendScore")
@SequenceGenerator(
		name = "seqTrendScoreGenerator",
		sequenceName = "seqTrendScore",
		allocationSize = 1
		)
@Getter
@Setter
@NoArgsConstructor
public class TrendScore {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTrendScoreGenerator")
	private Long seqTrendScore;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	private LocalDateTime baseDate;
	private Double scoreA;
	private Double scoreB;
	private Double finalScore;
	private Integer rank;
	
	private LocalDateTime createdAt;

}
