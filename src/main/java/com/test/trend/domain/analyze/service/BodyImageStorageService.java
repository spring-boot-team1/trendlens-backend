package com.test.trend.domain.analyze.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BodyImageStorageService {

    private final S3Template s3Template;
    @Value("${spring.cloud.aws.s3.bucket}")

    private String bucketName;

    @Value("${custom.s3.basePrefix}")
    private String basePrefix;         // uploads

    @Value("${custom.s3.prefix.bodyPhoto}")
    private String bodyPhotoPrefix;    // body-photo

    @Value("${custom.s3.urlPrefix}")
    private String urlPrefix; // https://trendlens.s3.ap-northeast-2.amazonaws.com

    private String buildKey(Long seqAccount, String originFilename){
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String safeName = originFilename == null ? "image" : originFilename.replace(" ","_");
        return basePrefix + "/" + bodyPhotoPrefix + "/" + seqAccount + "/" + time + "_" + safeName;
    }

    public String uploadBodyPhoto(Long seqAccount, MultipartFile file) throws IOException {
        String key = buildKey(seqAccount, file.getOriginalFilename());

        s3Template.upload(
                bucketName,
                key,
                file.getInputStream()
        );

        return "s3://" + bucketName + "/" + key;
    }

    public String uploadAndReturnKey(Long seqAccount, MultipartFile file) throws IOException {
        String key = buildKey(seqAccount, file.getOriginalFilename());
        s3Template.upload(bucketName, key, file.getInputStream());
        return key;
    }

    public String convertS3UriToUrl(String s3Uri) {
        URI uri = URI.create(s3Uri);
        String key = uri.getPath().substring(1);
        return urlPrefix + "/" + key;
    }

    public String extractKeyFromS3Uri(String s3Uri) {
        if (s3Uri == null || s3Uri.isBlank()) {
            return null;
        }

        final String prefix = "s3://";
        if (!s3Uri.startsWith(prefix)) {
            // 이미 key 형식("uploads/...")으로 들어온 경우 그대로 사용
            return s3Uri;
        }

        // "s3://trendlens/uploads/..." 에서 버킷 뒤 첫 '/' 위치 찾기
        int bucketEnd = s3Uri.indexOf('/', prefix.length()); // prefix 길이 이후 첫 '/'
        if (bucketEnd == -1 || bucketEnd + 1 >= s3Uri.length()) {
            throw new IllegalArgumentException("잘못된 S3 URI 형식입니다: " + s3Uri);
        }

        // bucket 뒤의 경로 부분만 key로 사용
        return s3Uri.substring(bucketEnd + 1);  // → uploads/analyze/mesh-photo/1/xxx.obj
    }

}
