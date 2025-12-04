package com.test.trend.domain.payment.trend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class UserTrendHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserTrendHistoryGenerator")
	@SequenceGenerator(name = "seqUserTrendHistoryGenerator", sequenceName = "seqUserTrendHistory", allocationSize = 1)
	private Long seqUserTrendHistory;
	
	private Long seqAccount;
	private Long seqKeyword;

	private LocalDateTime viewAt;
	private String sourcePage;
	
}
