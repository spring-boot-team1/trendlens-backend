package com.test.trend.domain.analyze.model;

public record StoredBodyPhotoDTO(
        String bucketName,
        String key,
        String url,
        String ext,
        String contentType) {
}
