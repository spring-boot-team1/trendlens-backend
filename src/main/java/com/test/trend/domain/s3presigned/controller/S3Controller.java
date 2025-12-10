package com.test.trend.domain.s3presigned.controller;

import com.test.trend.domain.s3presigned.dto.FileRequest;
import com.test.trend.domain.s3presigned.dto.PresignedURLResponse;
import com.test.trend.domain.s3presigned.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3Controller {
    private final S3Service s3Service;
    @PostMapping("/v1/presigned/{category}")
    public PresignedURLResponse getPresignedUrl(@PathVariable String category, @RequestBody FileRequest request) {
        String fileName = UUID.randomUUID() + "." + request.getExt();
        return s3Service.createPresignedUrl(category, fileName, request.getContentType());
    }
}
