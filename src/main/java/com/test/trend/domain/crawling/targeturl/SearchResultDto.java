package com.test.trend.domain.crawling.targeturl;

import lombok.Builder;

@Builder
public record SearchResultDto(
		String title,
		String url,
		String imageUrl

		){}
