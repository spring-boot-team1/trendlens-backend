package com.test.trend.domain.common.service;

import com.test.trend.config.S3Properties;
import com.test.trend.domain.common.dto.PresignedURLResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner presigner; //주입
    private final S3Properties prop;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     *
     * @param category S3/uploads 아래의 하위 폴더(profilepic, analyze, body-photo 등)
     * @param fileName 파일명(확장자 포함)
     * @param contentType MIME 타입
     * @return PresignedURLResponse
     */
    public PresignedURLResponse createPresignedUrl(String category, String fileName, String contentType) {
        //custom.s3.prefix에서 폴더명 가져오기
        String directory = prop.getPrefix().get(category);
        if (directory == null) {
            throw new IllegalArgumentException("Invalid Category(파일 저장 경로가 틀렸습니다.)" + category);
        }
        // 업로드 형식: uploads/profilepic/UUID.png, jpg, ...
        String key = prop.getBasePrefix() + "/" + directory + "/" + fileName;

        //S3 putObject 요청 만들기
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        //Presigned URL 생성
        PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(r ->
                r.putObjectRequest(objectRequest).signatureDuration(Duration.ofMinutes(5))
        );
        String finalURL = prop.getBaseUrl() + "/" + key;

        return new PresignedURLResponse(presignedPutObjectRequest.url().toString(), finalURL);
    }

}
