package com.test.trend.domain.crawling.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BatchStepLog")
@SequenceGenerator(
        name = "seqBatchStepLogGenerator",
        sequenceName = "seqBatchStepLog",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class BatchStepLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqBatchStepLogGenerator")
	private Long seqBatchStepLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seqBatchJob")
	private BatchJob seqBatchJob;

	private String stepName;
	private String status;
	private String errorMessage;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	
}
