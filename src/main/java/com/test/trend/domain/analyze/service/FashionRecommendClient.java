package com.test.trend.domain.analyze.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FashionRecommendClient {

    private final Client geminiClient;
}
