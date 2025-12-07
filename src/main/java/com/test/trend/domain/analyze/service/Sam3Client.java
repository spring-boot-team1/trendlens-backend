package com.test.trend.domain.analyze.service;

import com.test.trend.domain.analyze.model.Sam3BodyApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Slf4j
@Component
public class Sam3Client {

    private static final String SAM_BASE_URL = "";

    private final WebClient webClient;

    public Sam3Client() {
        this.webClient = WebClient.builder().baseUrl(SAM_BASE_URL).build();
    }

    public Sam3BodyApiResponse analyzeBody(
            MultipartFile file,
            double height,
            double weight
    ) throws IOException {
        // MultipartFile → ByteArrayResource (WebClient multipart 전송용)
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null
                        ? file.getOriginalFilename()
                        : "upload.jpg";
            }
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder
                .part("file", fileResource)
                .filename(fileResource.getFilename())
                .contentType(
                        file.getContentType() != null
                                ? MediaType.parseMediaType(file.getContentType())
                                : MediaType.APPLICATION_OCTET_STREAM
                );
        builder.part("height", String.valueOf(height));
        builder.part("weight", String.valueOf(weight));

        try {
            return webClient.post()
                    .uri("/analyze-body")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Sam3BodyApiResponse.class)
                    .block();
        }  catch (WebClientResponseException e) {
            log.error("[SAM3] FastAPI error status={}, body={}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw e;

        }

    }
}