package com.test.trend.domain.analyze.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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

    @Value("${custom.s3.base-prefix}")
    private String basePrefix;         // uploads

    @Value("${custom.s3.prefix.body-photo}")
    private String bodyPhotoPrefix;    // body-photo

    @Value("${custom.s3.url-prefix}")
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

}
