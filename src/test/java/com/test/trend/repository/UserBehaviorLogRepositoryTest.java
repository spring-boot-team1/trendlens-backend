package com.test.trend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.log.entity.UserBehaviorLog;
import com.test.trend.domain.payment.log.repository.UserBehaviorLogRepository;

@SpringBootTest
@Transactional
public class UserBehaviorLogRepositoryTest {

	@Autowired
    private UserBehaviorLogRepository repository;

    @Test
    void createLog() {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .seqAccount(1L)
                .eventType("CLICK")
                .eventDetail("메인 배너 클릭")
                .eventTime(LocalDateTime.now())
                .build();

        UserBehaviorLog saved = repository.save(log);

        assertThat(saved.getSeqLog()).isNotNull();
    }
	
}
