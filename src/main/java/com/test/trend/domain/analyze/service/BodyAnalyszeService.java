package com.test.trend.domain.analyze.service;

import com.test.trend.domain.analyze.entity.BodyAnalysis;
import com.test.trend.domain.analyze.entity.BodyMetrics;
import com.test.trend.domain.analyze.entity.BodyRecommendation;
import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;
import com.test.trend.domain.analyze.model.FashionRecommendDTO;
import com.test.trend.domain.analyze.model.Sam3dBodyApiResponse;
import com.test.trend.domain.analyze.repository.BodyAnalysisRepository;
import com.test.trend.domain.analyze.repository.BodyMetricsRepository;
import com.test.trend.domain.analyze.repository.BodyRecommendationRepository;
import com.test.trend.domain.analyze.service.BodyImageStorageService;
import com.test.trend.domain.analyze.service.FashionRecommendClient;
import com.test.trend.domain.analyze.service.Sam3dBodyClient;
import com.test.trend.domain.analyze.util.BodyAnalyzeMapper;
import com.test.trend.domain.s3presigned.service.S3Service;
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
    private final BodyRecommendationRepository bodyRecommendationRepository;
    private final FashionRecommendClient fashionRecommendClient;
    private final S3Service s3Service;

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
            // 1️⃣ 바디 사진 업로드 → key 반환 (DB에는 이 값 저장)
            String imageKey = bodyImageStorageService.uploadAndReturnKey(seqAccount, imageFile);

            // 2️⃣ SAM3D FastAPI 호출
            Sam3dBodyApiResponse apiResponse = sam3dBodyClient.analyzeBody(
                    imageFile,
                    heightCm,
                    weightKg,
                    seqAccount.toString(),
                    gender
            );

            Sam3dBodyApiResponse.Data data = apiResponse.getData();
            Sam3dBodyApiResponse.Metrics m = data.getMetrics();

            // SAM3D에서 내려온 메쉬 URI (예: s3://trendlens/uploads/analyze/mesh-photo/1/xxx.obj)
            String meshS3Uri = data.getMeshUrl();

            // 3️⃣ DTO는 먼저 “DB에 저장할 원본 값”으로 생성
            BodyAnalysisWithMetricsDTO dto = BodyAnalysisWithMetricsDTO.builder()
                    .seqAccount(seqAccount)
                    .imageUrl(imageKey)    // DB에는 key 저장
                    .meshUrl(meshS3Uri)    // DB에는 S3 URI(or 나중에 key로 변환해서) 저장
                    .heightCm(m.getHeightCm())
                    .weightKg(m.getWeightKg())
                    .bmi(m.getBmi())
                    .shoulderWidthCm(m.getShoulderWidthCm())
                    .armLengthCm(m.getArmLengthCm())
                    .legLengthCm(m.getLegLengthCm())
                    .torsoLengthCm(m.getTorsoLengthCm())
                    .gender(gender)
                    .build();

            // 4️⃣ DB 저장 (원본 값 기준)
            BodyAnalysis analysis = bodyAnalyzeMapper.toBodyAnalysis(dto, seqAccount);
            BodyAnalysis savedAnalysis = bodyAnalysisRepository.save(analysis);

            BodyMetrics metrics = bodyAnalyzeMapper.toBodyMetrics(dto, savedAnalysis);
            BodyMetrics savedMetrics = bodyMetricsRepository.save(metrics);

            dto.setSeqBodyAnalysis(savedAnalysis.getSeqBodyAnalysis());
            dto.setSeqBodyMetrics(savedMetrics.getSeqBodyMetrics());

            // 5️⃣ 저장 후 → 프론트 응답용으로 presigned URL로 덮어쓰기

            // 이미지: key → presigned GET URL
            String imagePreUrl = s3Service.createGetPresignedUrl(imageKey);

            // 메쉬: s3 URI → key 추출 → presigned GET URL
            String meshPreUrl = null;
            if (meshS3Uri != null && !meshS3Uri.isBlank()) {
                String meshKey = bodyImageStorageService.extractKeyFromS3Uri(meshS3Uri);
                meshPreUrl = s3Service.createGetPresignedUrl(meshKey);
            }

            dto.setImageUrl(imagePreUrl);
            dto.setMeshUrl(meshPreUrl);

            log.info("✅ 체형 분석 + 메트릭스 저장 완료: seqBodyAnalysis={}, seqBodyMetrics={}",
                    savedAnalysis.getSeqBodyAnalysis(), savedMetrics.getSeqBodyMetrics());

            // 6️⃣ 패션 추천 호출 (이미 dto에는 presigned URL이 들어있음)
            FashionRecommendDTO fashionRecommendDTO = fashionRecommendClient.AiResult(dto);

            dto.setPromptUsed(fashionRecommendDTO.getPromptUsed());
            dto.setAiResult(fashionRecommendDTO.getAiResult());

            BodyRecommendation recommendation = BodyRecommendation.builder()
                    .bodyMetrics(savedMetrics)
                    .promptUsed(fashionRecommendDTO.getPromptUsed())
                    .aiResult(fashionRecommendDTO.getAiResult())
                    .build();

            BodyRecommendation savedRec = bodyRecommendationRepository.save(recommendation);

            log.info("🧠 패션 추천 저장 완료: seqBodyRecommendation={}",
                    savedRec.getSeqBodyRecommendation());

            return dto;

        } catch (IOException e) {
            throw new RuntimeException("S3에 바디 사진 업로드 중 오류 발생", e);
        }
    }

}
