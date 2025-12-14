package com.test.trend.domain.crawling.insight;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
        String weekCode = DateUtil.weekCode(keyword.getCreatedAt().toLocalDate());

        // 1. 중복 체크
        if (weeklyInsightRepo.existsByKeywordAndWeekCode(keyword, weekCode)) {
            log.info("이미 금주 Insight 존재: {}", keyword.getKeyword());
            return;
        }

        // 2. 상위 콘텐츠 조회
        List<ContentDetail> topContents =
                contentDetailRepo.findTop5ByTargetUrlKeywordOrderByCrawledAtDesc(keyword);

        if (topContents.isEmpty()) {
            log.info("분석할 콘텐츠 없음: {}", keyword.getKeyword());
            return;
        }

        // 3. 프롬프트 생성
        String prompt = buildPrompt(keyword.getKeyword(), topContents);

        // 4. Gemini 호출
        String aiResponse = callGeminiApi(prompt);

        // 5. 파싱
        String[] parsed = parseAiResponse(aiResponse);

        // 6. 저장
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
        log.info("Weekly Insight 저장 완료: {}", keyword.getKeyword());
    }

    /**
     * ✅ 개선된 프롬프트 (UI 안정화 + 근거 강제)
     */
    private String buildPrompt(String keyword, List<ContentDetail> contents) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
당신은 패션 트렌드 리서처입니다.
아래 '본문 발췌'만 근거로 이번 주 '%s' 트렌드를 분석하세요.
과장, 단정, 허위 데이터 생성은 금지합니다.
근거가 불충분한 내용은 "추정"이라고 명시하세요.

[작성 규칙]
- 트렌드 요약: 최대 3줄, 각 줄은 40자 이내
- 스타일링 팁: 정확히 1문장
- 브랜드명/상품명 직접 언급 금지
- 본문에 없는 사실 생성 금지
- 담백한 리포트 문체 사용

[본문 발췌]
""".formatted(keyword));

        int idx = 1;
        for (ContentDetail content : contents) {
            String body = content.getBodyText();
            if (body == null || body.isBlank()) continue;

            body = body.replaceAll("\\s+", " ").trim();
            if (body.length() > 1200) {
                body = body.substring(0, 1200) + "...";
            }

            sb.append("[").append(idx++).append("] ")
                    .append(body)
                    .append("\n\n");
        }

        sb.append("""
[출력 형식]
반드시 한 줄로만 출력:
<요약1> / <요약2> / <요약3> ||| <스타일링 1문장>
""");

        return sb.toString();
    }

    private String callGeminiApi(String prompt) {
        try {
            String url = String.format(GEMINI_URL_TEMPLATE, geminiApiKey);

            GeminiRequest.Part part = new GeminiRequest.Part(prompt);
            GeminiRequest.Content content =
                    new GeminiRequest.Content(Collections.singletonList(part));
            GeminiRequest request =
                    new GeminiRequest(Collections.singletonList(content));

            GeminiResponse response =
                    restTemplate.postForObject(url, request, GeminiResponse.class);

            if (response != null
                    && response.getCandidates() != null
                    && !response.getCandidates().isEmpty()) {
                return response.getCandidates()
                        .get(0)
                        .getContent()
                        .getParts()
                        .get(0)
                        .getText();
            }
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            return "분석 실패 / 데이터 부족 ||| 잠시 후 다시 시도해주세요.";
        }
        return "데이터 없음 / 데이터 부족 ||| 데이터 없음";
    }

    /**
     * ✅ 파싱 안정화
     */
    private String[] parseAiResponse(String aiResponse) {
        String clean = aiResponse.replace("*", "").trim();
        String[] split = clean.split("\\|\\|\\|");

        if (split.length < 2) {
            return new String[]{
                    clean,
                    "기본 아이템과 매치해 실용적인 스타일을 연출해보세요."
            };
        }

        return new String[]{
                split[0].trim(),
                split[1].trim()
        };
    }
}
