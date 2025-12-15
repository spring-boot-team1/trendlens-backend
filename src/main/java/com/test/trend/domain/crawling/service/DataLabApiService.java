package com.test.trend.domain.crawling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.metric.DataLabRequestDto;
import com.test.trend.domain.crawling.metric.DataLabResponseDto;
import com.test.trend.domain.crawling.metric.TrendMetric;
import com.test.trend.domain.crawling.metric.TrendMetricRepository;
import com.test.trend.enums.YesNo;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DataLabApiService {

    private final TrendMetricRepository trendMetricRepo;
    private final KeywordRepository keywordRepo;
    private final ObjectMapper objectMapper;

    @Value("${naver.client-id}") // application.yml 설정
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;


    @Transactional
    public void fetchAndSaveTrend(Long seqKeyword) {

        System.out.println("========== [AUTH CHECK] ==========");
        System.out.println("CLIENT ID: " + clientId);       // 여기가 null인지 확인
        System.out.println("CLIENT SECRET: " + clientSecret); // 여기가 null인지 확인
        System.out.println("==================================");

        Keyword keywordEntity = keywordRepo.findById(seqKeyword)
                .orElseThrow(() -> new IllegalArgumentException("키워드 없음" + seqKeyword));

        //[핵심] 검색어 정제 로직 강화
        String originalKeyword = keywordEntity.getKeyword();
        String queryKeyword = originalKeyword
                .replaceAll("\\(.*?\\)", "")  // 괄호 안 내용 제거
                .replaceAll("\\[.*?\\]", "")  // 대괄호 안 내용 제거
                .replaceAll("-.*$", "")       // 하이픈(-) 뒤에 오는 모든 것 제거
                .replaceAll("[0-9]+(?i)color", "") // "3 COLOR" 같은 패턴 제거
                .trim();

        // 1. 날짜 설정(최근 30일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 2. 요청 Body 생성
        DataLabRequestDto requestDto = DataLabRequestDto.builder()
                .startDate(startDate.format(formatter))
                .endDate(endDate.format(formatter))
                .timeUnit("date")
                .keywordGroups(List.of (
                        DataLabRequestDto.KeywordGroup.builder()
                                .groupName(queryKeyword)
                                .keywords(Collections.singletonList(queryKeyword))
                                .build()
                ))
                .build();

        // 3. API 호출
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<DataLabRequestDto> request = RequestEntity
                .post("https://openapi.naver.com/v1/datalab/search") // 쇼핑 인사이트
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .header("Content-Type", "application/json")
                .body(requestDto);

        try {
            ResponseEntity<DataLabResponseDto> response = restTemplate.exchange(request, DataLabResponseDto.class);
            DataLabResponseDto body = response.getBody();

            if (body != null && body.getResults() != null && !body.getResults().isEmpty()) {
                List<DataLabResponseDto.Item> items = body.getResults().get(0).getData();

                //[핵심2] 데이터가 진짜 있을때만 저장 및 로그 출력
                if (items == null || items.isEmpty()) {
                    System.out.println(" >>> [DataLab] 검색 결과 없음 (데이터 0건):" + queryKeyword);
                    return;
                }

                // 4. DB 저장
                int saveCount = 0;
                for (DataLabResponseDto.Item item : items) {
                    LocalDate parsedDate = LocalDate.parse(item.getPeriod(), formatter);
                    Double ratio = item.getRatio();

                    // 전날 날짜
                    LocalDate prevDate = parsedDate.minusDays(1);

                    //전날 지표
                    TrendMetric prevMetric = trendMetricRepo
                            .findByKeyword_SeqKeywordAndBaseDate(seqKeyword, prevDate)
                            .orElse(null);

                    TrendMetric metric = trendMetricRepo
                            .findByKeyword_SeqKeywordAndBaseDate(seqKeyword, parsedDate)
                            .orElseGet(() -> createDefaultMetric(keywordEntity, parsedDate));
                    metric.setRatio(ratio);

                    try {
                        String json = objectMapper.writeValueAsString(item);
                        metric.setRawJson(json);
                    } catch (Exception e) {
                        System.out.println("[DataLab] RAW_JSON 직렬화 실패:" + e.getMessage());
                    }

                    //isHot 계산: 전날 대비   20% 이상 상승하면, Y
                    if (prevMetric != null && prevMetric.getRatio() != null) {
                        double threshold = prevMetric.getRatio() * 1.2;
                        if (ratio != null && ratio >= threshold) {
                            metric.setIsHot(YesNo.Y);
                        } else {
                            metric.setIsHot(YesNo.N);
                        }
                    }

                    trendMetricRepo.save(metric);
                }
                System.out.println(">>> [DataLab] 지표 저장 완료:" + keywordEntity.getKeyword());
            }
        } catch (Exception e) {
            System.out.println(">>> [Error] DataLab API 실패:" + e.getMessage());
        }
    }

    private TrendMetric createDefaultMetric(Keyword keywordEntity, LocalDate parsedDate) {
        return TrendMetric.builder()
                .keyword(keywordEntity)
                .baseDate(parsedDate)
                .isHot(YesNo.N)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
