package com.test.trend.domain.crawling.batch;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BatchLoggingService {

    private final BatchJobRepository batchJobRepo;
    private final BatchStepLogRepository batchStepLogRepo;

    @Transactional
    public BatchJob startJob() {
        BatchJob job = new BatchJob();
        job.setJobDate(LocalDateTime.now());
        job.setStartedAt(LocalDateTime.now());
        job.setStatus("RUNNING");
        job.setCreatedAt(LocalDateTime.now());
        return batchJobRepo.save(job);
    }

    @Transactional
    public void finishJob(Long seqBatchJob) {
        BatchJob job = batchJobRepo.findById(seqBatchJob).orElseThrow();
        job.setEndedAt(LocalDateTime.now());
        job.setStatus("COMPLETED");
        batchJobRepo.save(job);
    }

    @Transactional
    public void failJob(Long seqBatchJob, String errorMessage) {
        BatchJob job = batchJobRepo.findById(seqBatchJob).orElseThrow();
        job.setEndedAt(LocalDateTime.now());
        job.setStatus("FAILED");
        job.setErrorMessage(errorMessage);
        batchJobRepo.save(job);
    }

    @Transactional
    public void saveStepLog(Long seqBatchJob, String stepName, String status, String errorMessage) {
        BatchJob job = batchJobRepo.findById(seqBatchJob).orElseThrow();

        BatchStepLog log = new BatchStepLog();
        log.setSeqBatchJob(job);
        log.setStepName(stepName);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        log.setStartedAt(LocalDateTime.now());
        log.setEndedAt(LocalDateTime.now());

        batchStepLogRepo.save(log);
    }
}
