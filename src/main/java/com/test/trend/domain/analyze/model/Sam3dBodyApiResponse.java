package com.test.trend.domain.analyze.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Sam3dBodyApiResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Metrics metrics;

        @JsonProperty("mesh_url")
        private String meshUrl;

        @JsonProperty("mesh_path")
        private String meshPath;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metrics {
        @JsonProperty("height_cm")
        private BigDecimal heightCm;

        @JsonProperty("weight_kg")
        private BigDecimal weightKg;

        // FastAPI에서 key가 그냥 "bmi"라서 별도 @JsonProperty 필요 X
        private BigDecimal bmi;

        @JsonProperty("shoulder_width_cm")
        private BigDecimal shoulderWidthCm;

        @JsonProperty("arm_length_cm")
        private BigDecimal armLengthCm;

        @JsonProperty("leg_length_cm")
        private BigDecimal legLengthCm;

        @JsonProperty("torso_length_cm")
        private BigDecimal torsoLengthCm;

        // "M", "F", "U" 형태 그대로 들어옴
        private String gender;
    }
}
