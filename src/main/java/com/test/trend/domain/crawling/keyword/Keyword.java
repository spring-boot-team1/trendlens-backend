package com.test.trend.domain.crawling.keyword;

import java.time.LocalDateTime;

import com.test.trend.enums.YesNo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Keyword")
@SequenceGenerator(
		name = "seqKeywordGenerator",
		sequenceName = "seqKeyword",
		allocationSize = 1
		)
@Getter
@Setter
@NoArgsConstructor
public class Keyword {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqKeywordGenerator")
	private Long seqKeyword;
	private String keyword;
	private String category;
	
	@Enumerated(EnumType.STRING)
	private YesNo isActive = YesNo.Y;
	
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	

}
