package com.test.trend.domain.analyze.service;

import com.test.trend.domain.analyze.entity.BodyAnalysis;
import com.test.trend.domain.analyze.entity.BodyMetrics;
import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;
import com.test.trend.domain.analyze.model.Sam3dBodyApiResponse;
import com.test.trend.domain.analyze.repository.BodyAnalysisRepository;
import com.test.trend.domain.analyze.repository.BodyMetricsRepository;
import com.test.trend.domain.analyze.util.BodyAnalyzeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyAnalyszeService {

    private final BodyImageStorageService bodyImageStorageService;
    private final Sam3dBodyClient sam3dBodyClient;
    private final BodyAnalyzeMapper bodyAnalyzeMapper;
    private final BodyAnalysisRepository bodyAnalysisRepository;
    private final BodyMetricsRepository bodyMetricsRepository;

    /**
     * 1. 이미지 S3 업로드
     * 2. FastAPI(SAM3D) 호출
     * 3. 응답 + 입력값으로 DTO 구성
     * 4. 엔티티(BodyAnalysis / BodyMetrics) 저장
     */
    @Transactional
    public BodyAnalysisWithMetricsDTO analyzeAndSave(
            MultipartFile imageFile,
            BigDecimal heightCm,
            BigDecimal weightKg,
            String gender,
            Long seqAccount
    ) {
        try {
            String imageUrl = bodyImageStorageService.uploadBodyPhoto(seqAccount, imageFile);

            Sam3dBodyApiResponse apiResponse = sam3dBodyClient.analyzeBody(
                    imageFile,
                    heightCm,
                    weightKg,
                    seqAccount.toString(),
                    gender
            );

            Sam3dBodyApiResponse.Data data = apiResponse.getData();
            Sam3dBodyApiResponse.Metrics m = data.getMetrics();

            BodyAnalysisWithMetricsDTO dto = BodyAnalysisWithMetricsDTO.builder()
                    .seqAccount(seqAccount)
                    .imageUrl(imageUrl)
                    .meshUrl(data.getMeshUrl())
                    .heightCm(m.getHeightCm())
                    .weightKg(m.getWeightKg())
                    .bmi(m.getBmi())
                    .shoulderWidthCm(m.getShoulderWidthCm())
                    .armLengthCm(m.getArmLengthCm())
                    .legLengthCm(m.getLegLengthCm())
                    .torsoLengthCm(m.getTorsoLengthCm())
                    .gender(gender)
                    .build();

            BodyAnalysis analysis = bodyAnalyzeMapper.toBodyAnalysis(dto, seqAccount);
            BodyAnalysis savedAnalysis = bodyAnalysisRepository.save(analysis);

            BodyMetrics metrics = bodyAnalyzeMapper.toBodyMetrics(dto, savedAnalysis);
            BodyMetrics savedMetrics =  bodyMetricsRepository.save(metrics);

            dto.setSeqBodyAnalysis(savedAnalysis.getSeqBodyAnalysis());
            dto.setSeqBodyMetrics(savedMetrics.getSeqBodyMetrics());

            log.info("✅ 체형 분석 + 메트릭스 저장 완료: seqBodyAnalysis={}, seqBodyMetrics={}",
                    analysis.getSeqBodyAnalysis(), metrics.getSeqBodyMetrics());

            return dto;

        } catch (IOException e) {
            throw new RuntimeException("S3에 바디 사진 업로드 중 오류 발생", e);

        }

    }
}
