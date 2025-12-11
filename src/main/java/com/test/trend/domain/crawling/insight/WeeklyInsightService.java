package com.test.trend.domain.crawling.insight;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyInsightService {

    private final WeeklyInsightRepository weeklyInsightRepo;
    private final ContentDetailRepository contentDetailRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api-key-b}")
    private String geminiApiKey;

    private static final String GEMINI_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s";

    @Transactional
    public void createWeeklyInsight(Keyword keyword) {
        String weekCode = generateWeekCode(LocalDate.now());

        //1. 중복 확인
        if (weeklyInsightRepo.findByKeywordAndWeekCode(keyword, weekCode).isPresent()) {
            log.info("이미 금주 Insight 존재: {}", keyword.getKeyword());
            return;
        }

        //2. Top 콘텐츠 조회
        List<ContentDetail> topContents = contentDetailRepo.findTop5ByTargetUrlKeywordOrderByCrawledAtDesc(keyword);
        if (topContents.isEmpty()) {
            log.info("분석할 콘텐츠가 없습니다.: {}", keyword.getKeyword());
            return;
        }

        //3. 프롬프트 생성
        String prompt = buildPrompt(keyword.getKeyword(), topContents);

        //4. Gemini API 호출
        String aiResponse = callGeminiApi(prompt);

        //5. 파싱
        String[] parsed = parseAiResponse(aiResponse);

        //6. 저장
        WeeklyInsight insight = new WeeklyInsight();
        insight.setKeyword(keyword);
        insight.setWeekCode(weekCode);
        insight.setSummaryTxt(parsed[0]);
        insight.setStylingTip(parsed[1]);

        String sourceUrls = topContents.stream()
                .filter(c -> c.getTargetUrl() != null)
                .map(c -> c.getTargetUrl().getUrl())
                .collect(Collectors.joining(","));
        insight.setSourceUrls(sourceUrls);

        weeklyInsightRepo.save(insight);
        log.info("Gemini Insight 저장 완료: {}", insight.getKeyword());

    }

    private String buildPrompt(String keyword, List<ContentDetail> contents) {
        StringBuilder sb = new StringBuilder();
        sb.append("패션 트렌드 분석가로서 '").append(keyword).append("'의 이번주 트렌드를 분석해줘.\n");
        sb.append("참고 자료(본문 내용):\n");

        for (int i = 0; i < contents.size(); i++) {
            ContentDetail content = contents.get(i);
            String body = content.getBodyText();

            //본문 없으면 스킵
            if (body == null || body.isBlank()) continue;

            //너무 길면 1000자에서 자름(토큰 절약 및 에러 방지)
            if (body.length() > 1000) {
                body = body.substring(0, 1000) + "...";
            }
            sb.append("[").append(i + 1).append("]").append(body).append("\n\n");
        }

        //AI에게 구분자 (|||) 사용을 강제함
        sb.append("\n응답 형식은 반드시 아래와 같이 작성해주세여:\n");
        sb.append("트렌드 요약(3줄 이내로 서술 ||| 스타일링 팁(1문장 추천)");
        return sb.toString();
    }

    private String callGeminiApi(String prompt) {
        try {
            String url = String.format(GEMINI_URL_TEMPLATE, geminiApiKey, geminiApiKey);

            //Gemini 요천 객체 조힙
            GeminiRequest.Part part = new GeminiRequest.Part(prompt);
            GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
            GeminiRequest request = new GeminiRequest(Collections.singletonList(content));

            GeminiResponse response = restTemplate.postForObject(url, request, GeminiResponse.class);

            if (response != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            return "분석 실패 ||| 잠시 후 다시 시도해주세요.";
        }
        return "데이터 없음 ||| 데이터 없음";
    }

    private String[] parseAiResponse(String aiResponse) {
        // Gemini가 가끔 ** 같은 마크다운을 붙일 때가 있어 제거
        String cleanText = aiResponse.replace("*", "").trim();

        //구분자 |||로 나눔
        String[] split = cleanText.split("\\|\\|\\|");

        if (split.length < 2) {
            //구분자가 없으면 전체를 요약으로 간주
            return new String[]{cleanText, "추천 팁을 생성하지 못했습니다."};
        }
        return new String[]{split[0].trim(), split[1].trim()};
    }
    private String generateWeekCode(LocalDate date) {
        int year = date.get(IsoFields.WEEK_BASED_YEAR);
        int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return String.format("%d-W%02d", year, week);
    }
}
