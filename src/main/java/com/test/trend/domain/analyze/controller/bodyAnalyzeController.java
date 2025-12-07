package com.test.trend.domain.analyze.controller;

import com.test.trend.domain.analyze.model.BodyAnalyzeResponse;
import com.test.trend.domain.analyze.model.Sam3BodyApiResponse;
import com.test.trend.domain.analyze.model.StoredBodyPhotoDTO;
import com.test.trend.domain.analyze.service.S3BodyPhotoStorage;
import com.test.trend.domain.analyze.service.Sam3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/analyze")
@RequiredArgsConstructor
public class bodyAnalyzeController {

    private final S3BodyPhotoStorage s3BodyPhotoStorage;
    private final Sam3Client sam3Client;
    /**
     * 체형 분석 + 사진 업로드 통합 엔드포인트
     *
     * - URL: POST /api/analyze/body-photo
     * - Content-Type: multipart/form-data
     *
     * 요청 파라미터:
     *  - username : (임시) 사용자 아이디 (나중에 Security Principal에서 꺼내도 됨)
     *  - file     : 업로드할 전신 사진 (MultipartFile)
     *  - height   : 키 (cm)
     *  - weight   : 몸무게 (kg)
     *
     * 응답:
     *  - S3에 저장된 사진 정보(버킷, 키, URL, 확장자 등)
     *  - SAM3 체형 분석 결과
     */
    @PostMapping(
            value = "/body-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BodyAnalyzeResponse> analyzeBodyPhoto(
            @RequestParam("username") String username,
            @RequestPart("file") MultipartFile file,
            @RequestParam("height") double height,
            @RequestParam("weight") double weight
    ) throws IOException {
        log.info("[Analyze] username={}, height={}, weight={}", username, height, weight);
        StoredBodyPhotoDTO storedPhoto = s3BodyPhotoStorage.uploadBodyPhoto(username, file);
        Sam3BodyApiResponse bodyAnalysis = sam3Client.analyzeBody(file, height, weight);
        BodyAnalyzeResponse response = BodyAnalyzeResponse.builder()
                .photo(storedPhoto)
                .bodyAnalysis(bodyAnalysis)
                .build();

        return ResponseEntity.ok(response);
    }
}
