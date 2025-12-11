package com.test.trend.domain.crawling.insight;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;

import jakarta.persistence.*;
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

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}


