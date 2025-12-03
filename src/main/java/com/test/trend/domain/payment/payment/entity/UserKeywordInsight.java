package com.test.trend.domain.payment.payment.entity;

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
public class UserKeywordInsight {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserKeywordInsightGenerator")
	@SequenceGenerator(name = "seqUserKeywordInsightGenerator", sequenceName = "seqUserKeywordInsight", allocationSize = 1)
	private Long seqUserKeywordInsight;
	
	private Long seqAccount;
	private Long seqKeyword;
	
	@Lob
	private String insightText;
	
	private Double trendScore;
	private String hotYn; // Y/N
	
	private LocalDateTime createdAt;
	
}
