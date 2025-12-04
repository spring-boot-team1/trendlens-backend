package com.test.trend.domain.crawling.targeturl;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.TargetUrlStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TargetUrl")
@SequenceGenerator(
        name = "seqTargetUrlGenerator",
        sequenceName = "seqTargetUrl",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUrl {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTargetUrlGenerator")
	private Long seqUrl;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqKeyword")
	private Keyword keyword;
	private String url;
	private String title;
	private LocalDateTime postDate;
	private String domain;
	
	@Enumerated(EnumType.STRING)
	private TargetUrlStatus status;
	private LocalDateTime createdAt;
	
	@PrePersist
	public void prePersist() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (status == null)	{
			status = TargetUrlStatus.WAIT;
		}
	}
	
}
