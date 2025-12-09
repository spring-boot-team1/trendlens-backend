package com.test.trend.domain.crawling.score;

import java.time.LocalDate;
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
import lombok.*;

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
@AllArgsConstructor
@Builder
public class TrendScore {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTrendScoreGenerator")
	private Long seqTrendScore;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	private LocalDate baseDate;
	private Double scoreA;
	private Double scoreB;
	private Double finalScore;
	private Integer rank;
	
	private LocalDateTime createdAt;

}
