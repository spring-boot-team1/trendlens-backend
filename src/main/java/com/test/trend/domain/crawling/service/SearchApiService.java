package com.test.trend.domain.crawling.service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchApiService {

    @Value("${naver.search.base-url}")
    private String baseUrl;
    
	@Value("${naver.search.client-id}")
	private String clientId;
	
	@Value("${naver.search.client-secret}")
	private String clientSecret;
	
	private final RestTemplate restTemplate = new RestTemplate();

	public Map callNaverApi(String keyword) {
		
		String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
		String url = baseUrl + "?query=" + encoded + "&display=20";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<Map> response = 
					restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		
		return response.getBody();
	}
	
	public List<SearchResultDto> search(String keyword) {
		
		Map body = callNaverApi(keyword);
		
		List<Map<String, Object>> items = 
				(List<Map<String, Object>>) b
	}
}
