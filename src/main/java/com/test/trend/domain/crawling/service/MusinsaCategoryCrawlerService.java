package com.test.trend.domain.crawling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.crawling.targeturl.TargetUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusinsaCategoryCrawlerService {

    private final TargetUrlRepository targetUrlRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Connection
}
