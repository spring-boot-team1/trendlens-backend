package com.test.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.entity.TrendSnapshot;
import com.test.trend.domain.payment.repository.TrendSnapshotRepository;

@SpringBootTest
@Transactional
public class TrendSnapshotRepositoryTest {

	@Autowired
    private TrendSnapshotRepository repository;

    @Test
    void createSnapshot() {
        TrendSnapshot snapshot = TrendSnapshot.builder()
                .snapshotDate(LocalDateTime.now())
                .topBrand("{\"brand\": \"Nike\"}")
                .topCategory("{\"category\": \"Shoes\"}")
                .styleTrend("{\"style\": \"Street\"}")
                .createdAt(LocalDateTime.now())
                .build();

        TrendSnapshot saved = repository.save(snapshot);

        assertThat(saved.getSeqTrendSnapshot()).isNotNull();
    }
	
}
