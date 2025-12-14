package com.test.trend.domain.crawling.batch;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BatchJob")
@SequenceGenerator(
        name = "seqBatchJobGenerator",
        sequenceName = "seqBatchJob",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
public class BatchJob {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqBatchJobGenerator")
	private Long seqBatchJob;
	
	private LocalDateTime jobDate;
	private String status;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private String errorMessage; 
	private LocalDateTime createdAt;

}
