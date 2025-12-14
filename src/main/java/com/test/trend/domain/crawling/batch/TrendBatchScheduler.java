package com.test.trend.domain.crawling.batch;

import com.test.trend.domain.crawling.service.FinalTrendPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendBatchScheduler {

    private final FinalTrendPipelineService finalTrendPipelineService;
    private final BatchLoggingService loggingService;

    @Scheduled(cron = "0 0 3 * * *")
    //@Scheduled(initialDelay = 5000, fixedDelay = 99999999)
    public void scheduleDailyTrendAnalysis() {
        log.info("⏰ [Scheduler] 새벽 3시 정기 트렌드 분석 시작");

        BatchJob job = loggingService.startJob();
        Long seqBatchJob = job.getSeqBatchJob();

        try {
            // 2. 파이프라인 실행
            finalTrendPipelineService.executeFullPipeline();

            //3. 성공 기록
            loggingService.finialJob(seqBatchJob);
            loggingService.saveStepLog(seqBatchJob, "FULL_PIPELINE", "SUCCESS", "ALL steps completed successfully");

            log.info("⏰ [Scheduler] 정기 분석 정상 종료 (Job ID: {})", seqBatchJob);
        } catch (Exception e) {
            // 4. 실패 기록
            loggingService.failjob(seqBatchJob, e.getMessage());
            loggingService.saveStepLog(seqBatchJob, "FULL_PIPELINE", "FAILED", e.getMessage());

            log.error("⏰ [Scheduler] 정기 분석 중 오류 발생! (Job ID: {})", seqBatchJob, e);
        }
    }
}
