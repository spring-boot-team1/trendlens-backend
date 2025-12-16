package com.test.trend.domain.analyze.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;
import com.test.trend.domain.analyze.model.FashionRecommendDTO;
import com.test.trend.domain.analyze.util.FashionPromptFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FashionRecommendClient {

    private final Client geminiClient;


    public FashionRecommendClient(@Value("${gemini.api-key}") String apiKey) {

        this.geminiClient = Client.builder().apiKey(apiKey).build();
    }

    public FashionRecommendDTO AiResult(BodyAnalysisWithMetricsDTO dto){

        String metricsJson = """
                {
                  "height_cm": %s,
                  "weight_kg": %s,
                  "bmi": %s,
                  "shoulder_width_cm": %s,
                  "arm_length_cm": %s,
                  "leg_length_cm": %s,
                  "torso_length_cm": %s,
                  "gender": "%s"
                }
                """.formatted(
                dto.getHeightCm(),       // BigDecimal -> toString()
                dto.getWeightKg(),
                dto.getBmi(),
                dto.getShoulderWidthCm(),
                dto.getArmLengthCm(),
                dto.getLegLengthCm(),
                dto.getTorsoLengthCm(),
                dto.getGender()          // "M" / "F" / "U"
        );

        String prompt = FashionPromptFactory.buildFashionPrompt(metricsJson);

        try{
            GenerateContentResponse response = geminiClient.models.generateContent(
                    "gemini-2.5-flash",prompt,null
            );

            String answer = response.text();
            log.info("Gemini-2.5-flash answer is {}", answer);

            return new FashionRecommendDTO(prompt, answer);
        } catch (Exception e) {
            log.error("❌ Gemini 패션 추천 생성 실패", e);
            throw new RuntimeException("Gemini 패션 추천 호출 중 오류 발생", e);
        }

    }

}


