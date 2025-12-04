package com.test.trend.domain.crawling.insight;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "WeeklyInsight")
@SequenceGenerator(
        name = "seqWeeklyGenerator",
        sequenceName = "seqWeekly",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class WeeklyInsight {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqWeeklyGenerator")
	private Long seqWeekly;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	private String weekCode;
	
	@Lob
	private String summaryTxt;
	
	@Lob
	private String stylingTip;
	private String sourceUrls;
	private LocalDateTime createdAt;

}
