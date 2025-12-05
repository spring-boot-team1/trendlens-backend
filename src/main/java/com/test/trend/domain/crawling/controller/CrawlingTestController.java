package com.test.trend.domain.crawling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.crawling.service.CrawlingTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CrawlingTestController {
	
	private final CrawlingTestService crawlingTestService;
	
	@GetMapping("/api/test/crawl")
	public String testCrawl(
			@RequestParam("arg0") String site,
			@RequestParam("arg1") String url	
	) {
		return crawlingTestService.testFetch(url, site);
	}

}
