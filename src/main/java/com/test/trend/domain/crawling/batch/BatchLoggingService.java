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

    //배치 작업 시작 (Job 생성)
    @Transactional
    public BatchJob startJob() {
        BatchJob job = new BatchJob();
        job.setJobDate(LocalDateTime.now());
        job.setStartedAt(LocalDateTime.now());
        job.setStatus("RUNNING");
        job.setCreatedAt(LocalDateTime.now());
        return batchJobRepo.save(job);
    }

    // 2. 배치 작업 종료(성공)
    @Transactional
    public void finialJob(Long seqBatchJob) {
        BatchJob job = batchJobRepo.findById(seqBatchJob).orElseThrow();
        job.setEndedAt(LocalDateTime.now());
        job.setStatus("COMPLETED");
        batchJobRepo.save(job);
    }

    //3. 배치 작업 실패 (에러 기록)
    @Transactional
    public void failjob(Long seqBatchJob, String errorMessage) {
        BatchJob job = batchJobRepo.findById(seqBatchJob).orElseThrow();
        job.setEndedAt(LocalDateTime.now());
        job.setStatus("FAILED");
        job.setErrorMessage(errorMessage);
        batchJobRepo.save(job);
    }

    //4. 단계별 로그 기록 (StepLog)
    @Transactional
    public void saveStepLog(Long seqBathJob, String stepName, String status, String errorMessage ) {
        BatchStepLog log = new BatchStepLog();
        log.setSeqBatchJob(seqBathJob);
        log.setStepName(stepName);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        log.setStartedAt(LocalDateTime.now());
        log.setEndedAt(LocalDateTime.now());
        batchStepLogRepo.save(log);
    }



}
