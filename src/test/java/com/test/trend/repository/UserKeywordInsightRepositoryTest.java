package com.test.trend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.trend.entity.UserKeywordInsight;
import com.test.trend.domain.payment.trend.repository.UserKeywordInsightRepository;

@SpringBootTest
@Transactional
public class UserKeywordInsightRepositoryTest {

	@Autowired
    private UserKeywordInsightRepository repository;

    @Test
    void createInsight() {
        UserKeywordInsight insight = UserKeywordInsight.builder()
                .seqAccount(1L)
                .seqKeyword(10L)
                .insightText("해당 키워드가 최근 30일간 급상승")
                .trendScore(82.5)
                .hotYn("Y")
                .createdAt(LocalDateTime.now())
                .build();

        UserKeywordInsight saved = repository.save(insight);

        assertThat(saved.getSeqUserKeywordInsight()).isNotNull();
    }
	
}
