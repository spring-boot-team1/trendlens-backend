package com.test.trend.domain.analyze.util;

import com.test.trend.domain.analyze.entity.BodyAnalysis;
import com.test.trend.domain.analyze.entity.BodyMetrics;
import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;
import com.test.trend.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BodyAnalyzeMapper {


    public BodyAnalysis toBodyAnalysis(BodyAnalysisWithMetricsDTO dto, Long seqAccount) {
        return BodyAnalysis.builder()
                .seqAccount(seqAccount)
                .imageUrl(dto.getImageUrl())
                .meshUrl(dto.getMeshUrl())
                .heightCm(dto.getHeightCm())
                .weightKg(dto.getWeightKg())
                .gender(Gender.fromCode(dto.getGender()))   // "M", "F", "U"
                // 상태값 정책: 최초 분석 완료 시 DONE 으로 고정
                .status("DONE")
                .build();
    }

    public BodyMetrics toBodyMetrics(BodyAnalysisWithMetricsDTO dto, BodyAnalysis analysis) {
        return BodyMetrics.builder()
                .bodyAnalysis(analysis)
                .bmi(dto.getBmi())
                .shoulderWidthCm(dto.getShoulderWidthCm())
                .armLengthCm(dto.getArmLengthCm())
                .legLengthCm(dto.getLegLengthCm())
                .torsoLengthCm(dto.getTorsoLengthCm())
                .build();
    }
}
