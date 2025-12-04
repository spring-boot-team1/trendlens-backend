package com.test.trend.domain.crawling.interest;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AccountKeyword")
@SequenceGenerator(
        name = "seqAcKeywordGenerator",
        sequenceName = "seqAcKeyword",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class AccountKeyword {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAcKeywordGenerator")
	private Long seqAcKeyword;
	private Long seqAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	@Enumerated(EnumType.STRING)
	private YesNo alterYn = YesNo.N;
	private LocalDateTime createdAt;

}
