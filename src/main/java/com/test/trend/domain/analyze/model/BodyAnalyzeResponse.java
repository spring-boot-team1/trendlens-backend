package com.test.trend.domain.analyze.model;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyAnalyzeResponse {
    private StoredBodyPhotoDTO photo;
    private Sam3BodyApiResponse bodyAnalysis;

}
