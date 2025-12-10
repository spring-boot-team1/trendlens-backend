package com.test.trend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties(prefix = "custom.s3")
@Getter
@Setter
public class S3Properties {
    private String baseUrl; //https://...amazonaws.com
    private String basePrefix; // uploads
    private Map<String, String> prefix; // {profilepic=profilepic, analyze=analyze ...}
}
