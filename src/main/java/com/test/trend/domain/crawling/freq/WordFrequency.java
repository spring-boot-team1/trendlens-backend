package com.test.trend.domain.crawling.freq;

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
@Table(name = "WordFrequency")
@SequenceGenerator(
        name = "seqWordFreqGenerator",
        sequenceName = "seqWordFreq",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class WordFrequency {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqWordFreqGenerator")
	private Long seqWordFreq;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	
	private String word;
	private Integer count;
	private LocalDateTime analyzedAt;
	private long analysisBatch;	
}
