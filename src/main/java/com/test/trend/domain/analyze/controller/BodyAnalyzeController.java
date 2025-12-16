package com.test.trend.domain.analyze.controller;

import com.test.trend.domain.analyze.model.BodyAnalysisWithMetricsDTO;
import com.test.trend.domain.analyze.service.BodyAnalyszeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/analyze")
@RequiredArgsConstructor
@Tag(
        name = "Body Analyze",
        description = "SAM3D 기반 체형 분석 + 패션 추천 API"
)
public class BodyAnalyzeController {

    private final BodyAnalyszeService bodyAnalyszeService;

    @PostMapping(value = "/body", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "체형 분석 및 패션 추천",
            description = """
                    사용자가 업로드한 **전신 사진**과 키/몸무게/성별/계정 PK를 받아서,
                    1) 이미지를 S3에 저장하고  
                    2) FastAPI(SAM 3D Body)로 체형 분석을 수행한 뒤  
                    3) Gemini에게 패션 추천을 요청하여  
                    최종적으로 **체형 메트릭스 + AI 패션 추천 결과**를 모두 반환합니다.
                    """)
    public ResponseEntity<BodyAnalysisWithMetricsDTO> analyzeBody
            (
                    @RequestPart("imageFile") MultipartFile imageFile,
                    @RequestParam("heightCm") BigDecimal heightCm,
                    @RequestParam("weightKg") BigDecimal weightKg,
                    @RequestParam("gender") String gender,
                    @RequestParam("seqAccount") Long seqAccount   // 일단 하드코딩 버전
                    // @AuthenticationPrincipal CustomUserDetails userDetails,
            )
    {

        // 토큰에서 꺼낸 계정 PK 사용 (메서드명은 네 프로젝트에 맞게 수정)
        //Long seqAccount = userDetails.getSeqAccount();
        // 혹은
        // Long seqAccount = userDetails.getAccount().getSeqAccount();
        BodyAnalysisWithMetricsDTO dto = bodyAnalyszeService.analyzeAndSave(imageFile, heightCm, weightKg, gender, seqAccount);

        return ResponseEntity.ok(dto);
    }

}
