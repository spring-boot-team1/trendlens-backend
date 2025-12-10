package com.test.trend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "custom.s3")
@Getter
@Setter
public class S3Properties {
    private String urlPrefix; //https://...amazonaws.com
    private String basePrefix; // uploads
    private Map<String, String> prefix; // {profilepic=profilepic, analyze=analyze ...}
}
