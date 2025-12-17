package com.test.trend.domain.crawling.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.crawling.service.SearchApiService;
import com.test.trend.domain.crawling.targeturl.SearchResultDto;

import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class SearchApiController {
	
	private final SearchApiService searchApiService;
	
	@GetMapping("/naver-search")
	public List<SearchResultDto> testSearch(@RequestParam("keyword") String keyword){
		return searchApiService.searchBlogUrls(keyword);
	}

}
