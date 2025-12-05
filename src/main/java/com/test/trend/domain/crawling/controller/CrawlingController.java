package com.test.trend.domain.crawling.controller;

import com.test.trend.domain.crawling.service.MusinsaCategoryCrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.crawling.service.CrawlingTestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlingController {
	
	private final MusinsaCategoryCrawlerService musinsaCrawler;
	
	@GetMapping("/musinsa")
	public String crawlMusinsa(
			@RequestParam String categoryUrl,
			@RequestParam Long seqKeyword
	) {
		try {
            int cnt = musinsaCrawler.crawlCategory(categoryUrl, seqKeyword);
            return "Inserted: " + cnt;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
	}

}
