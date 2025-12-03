package com.test.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.payment.entity.UserTrendHistory;
import com.test.trend.domain.payment.payment.repository.UserTrendHistoryRepository;

@SpringBootTest
@Transactional
public class UserTrendHistoryRepositoryTest {

	@Autowired
    private UserTrendHistoryRepository repository;

    @Test
    void createHistory() {
        UserTrendHistory history = UserTrendHistory.builder()
                .seqAccount(1L)
                .seqKeyword(10L)
                .viewAt(LocalDateTime.now())
                .sourcePage("trend/home")
                .build();

        UserTrendHistory saved = repository.save(history);

        assertThat(saved.getSeqUserTrendHistory()).isNotNull();
    }
	
}
