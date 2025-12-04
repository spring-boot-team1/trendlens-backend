package com.test.trend.domain.crawling.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.crawling.targeturl.TargetUrlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/target-url")
public class TargetUrlController {
	
	private final TargetUrlService targetUrlService;
	
	@PostMapping("/collect/{seqKeyword}")
	public Map<String, Object> collect(@PathVariable("seqKeyword") Long seqKeyword) {
		
		int savedCount = targetUrlService.collectTargets(seqKeyword);
		
		return Map.of(
				"seqKeyword",seqKeyword,
				"savedCount",savedCount
		);
	}
	
	@GetMapping("/wait/count")
	public Map<String, Object> waitCount() {
		int count = targetUrlService.getWaitUrls().size();
		return Map.of("waitCount", count);
	}

}
