package com.test.trend.domain.crawling.targeturl;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record SearchResultDto(
		String title,
		String url,
		LocalDateTime postDate
		){}
