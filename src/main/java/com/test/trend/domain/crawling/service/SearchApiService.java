package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.targeturl.SearchResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchApiService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    // 네이버 날짜 포맷 (예: 20241208) 처리를 위한 포맷터
    private static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<SearchResultDto> searchBlogUrls(String keyword) {
        List<SearchResultDto> results = new ArrayList<>();

        try {
            // 1. 요청 URL 생성
            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/blog.json")
                    .queryParam("query", keyword + " 코디") // 검색어 강화
                    .queryParam("display", 3)
                    .queryParam("sort", "sim")
                    .encode()
                    .build()
                    .toUri();

            // 2. 헤더 설정
            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<Void> req = RequestEntity
                    .get(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .build();

            // 3. 요청 및 응답 처리
            ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

                for (Map<String, Object> item : items) {
                    // 4. 날짜 파싱 (String -> LocalDateTime)
                    String postDateStr = (String) item.get("postdate");
                    LocalDateTime postDateTime = LocalDateTime.now(); // 기본값

                    try {
                        if (postDateStr != null && !postDateStr.isBlank()) {
                            LocalDate date = LocalDate.parse(postDateStr, NAVER_DATE_FORMATTER);
                            postDateTime = date.atStartOfDay(); // 00:00:00으로 설정
                        }
                    } catch (Exception e) {
                        System.out.println("날짜 파싱 실패: " + postDateStr);
                        // 파싱 실패 시 현재 시간 유지
                    }

                    // 5. DTO 생성 (Builder 사용)
                    SearchResultDto dto = SearchResultDto.builder()
                            .title((String) item.get("title"))
                            .url((String) item.get("link"))           // JSON의 link -> DTO의 url
                            .description((String) item.get("description"))
                            .postDate(postDateTime)                   // 파싱된 날짜 주입
                            .build();

                    results.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
