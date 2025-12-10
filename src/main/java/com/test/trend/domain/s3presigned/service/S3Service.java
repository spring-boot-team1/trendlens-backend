package com.test.trend.domain.s3presigned.service;

import com.test.trend.config.S3Properties;
import com.test.trend.domain.s3presigned.dto.PresignedURLResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
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
        String finalURL = prop.getUrlPrefix() + "/" + key;

        return new PresignedURLResponse(presignedPutObjectRequest.url().toString(), finalURL);
    }

    /**
     * 이미 S3에 올라가 있는 객체를 조회하기 위한 presigned GET URL 생성
     *
     * @param objectKey 예: "uploads/analyze/mesh-photo/1/xxxx.obj"
     * @param duration  URL 유효기간 (예: Duration.ofMinutes(10))
     * @return          브라우저/three.js에서 바로 GET 할 수 있는 URL
     *
     */
     public String createGetPresigedUrl(String objectKey, Duration duration){
         GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                 .bucket(bucket)
                 .key(objectKey)
                 .build();

         GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                 .signatureDuration(duration)
                 .getObjectRequest(getObjectRequest)
                 .build();

         PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
         return presigned.url().toString();
     }

    /**
     * 10분짜리 GET presigned URL
     */
    public String createGetPresignedUrl(String objectKey){
        return createGetPresigedUrl(objectKey, Duration.ofMinutes(10));
    }


}
