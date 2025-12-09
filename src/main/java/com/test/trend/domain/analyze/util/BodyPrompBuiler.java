package com.test.trend.domain.analyze.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BodyPrompBuiler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String buildMetricsJson(BodyAnalysisWithMetricsDTO dto){
        try {
            Map<String, Object> metricsMap = new LinkedHashMap<>();
            metricsMap.put("height_cm", dto.getHeightCm());
            metricsMap.put("weight_kg", dto.getWeightKg());
            metricsMap.put("bmi", dto.getBmi());
            metricsMap.put("shoulder_width_cm", dto.getShoulderWidthCm());
            metricsMap.put("leg_length_cm", dto.getLegLengthCm());
            metricsMap.put("torso_length_cm", dto.getTorsoLengthCm());
            metricsMap.put("gender", dto.getGender());

            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("metrics", metricsMap);

            return objectMapper.writeValueAsString(wrapper);
        }catch (JsonProcessingException e){
            throw new RuntimeException("metrics JSON 직렬화 실패", e);
        }
    }


}
