package com.test.trend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {
    /**
     * S3에 접근하는 객체 생성
     * @return S3 접근 객체
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder().region(Region.AP_NORTHEAST_2).build();
    }

    /**
     * Presigned URL 생성하는 Presigner 객체 생성
     * @return Presigner 객체
     */
    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder().region(Region.AP_NORTHEAST_2).build();
    }
}
