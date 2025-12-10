package com.test.trend.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedURLResponse {
    private String presignedURL; //프론트가 put할 URL
    private String fileURL; // 저장될 최종 S3 URL
}
