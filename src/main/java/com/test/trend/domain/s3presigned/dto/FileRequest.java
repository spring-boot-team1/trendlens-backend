package com.test.trend.domain.s3presigned.dto;

import lombok.Getter;

@Getter
public class FileRequest {
    private String ext; //확장자
    private String contentType; //MIME 타입
}
