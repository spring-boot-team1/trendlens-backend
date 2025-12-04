package com.test.trend.domain.crawling.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.test.trend.domain.crawling.targeturl.SearchResultDto;
import com.test.trend.domain.crawling.util.DateUtil;
import com.test.trend.domain.crawling.util.HtmlCleaner;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchApiService {

    @Value("${naver.search.base-url}")
    private String baseUrl;
    
	@Value("${naver.search.client-id}")
	private String clientId;
	
	@Value("${naver.search.client-secret}")
	private String clientSecret;
	
	private final RestTemplate restTemplate = new RestTemplate();

 
	public Map<String, Object> callNaverApi(String keyword) {
		
		String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
		String url = baseUrl + "?query=" + encoded + "&display=20";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<Map> response = 
					restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		
		 System.out.println("[NAVER-RAW] status=" + response.getStatusCode());
		 System.out.println("[NAVER-RAW] url=" + url);
		 System.out.println("[NAVER-RAW] body=" + response.getBody());
		
		return (Map<String, Object>)response.getBody();
	}
	
	public List<SearchResultDto> search(String keyword) {
		
		Map<String, Object> body = callNaverApi(keyword);
		
		if (body == null) {
			System.out.println("[NAVER] body is null");
			return List.of();
		}
		
		List<Map<String, Object>> items = 
				(List<Map<String, Object>>) body.get("items");
		
		System.out.println("[NAVER] keyword=" + keyword 
				+ ", items=" + (items == null ? "null" : items.size()));
		
		if(items == null) {
			System.out.println("[NAVER] keys =" + body.keySet());
			return List.of();
		}
		
		String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
		List<String> tokens = Arrays.stream(normalizedKeyword.split("\\s+"))
				.filter(s -> !s.isBlank())
				.toList();
	
		return items.stream()
	            .map(i -> {
	            	String rawTitle = (String) i.get("title");
	            	String link 	= (String) i.get("link");
	            	String pubDate 	= (String) i.get("pubDate");
	            	
	            	String cleanTitle = HtmlCleaner.clean(rawTitle);
	            	
	            	LocalDateTime postDate = parsePostDate(postDate);
	            		
	            	
	            	return SearchResultDto.builder()
	            			.title(cleanTitle)
	            			.url(link)
	            			.postDate(DateUtil.parsePostDate(pubDate))
	            			.build();
	            })
	            .toList();
	}
}
