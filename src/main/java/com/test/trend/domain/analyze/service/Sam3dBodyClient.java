package com.test.trend.domain.analyze.service;

import com.test.trend.domain.analyze.model.Sam3dBodyApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
@Slf4j
public class Sam3dBodyClient {

    private static final String BASE_URL = "";
    private final WebClient webClient;

    public Sam3dBodyClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    /**
     * FastAPI /analyze-body 호출
     *
     * @param imageFile  사용자가 업로드한 원본 이미지 (MultipartFile)
     * @param heightCm   키(cm)
     * @param weightKg   몸무게(kg)
     * @param seqAccount   FastAPI에 넘길 username (우린 seqAccount.toString() 쓸 예정)
     * @param gender     "M" / "F" / "U"
     */
    public Sam3dBodyApiResponse analyzeBody(
            MultipartFile imageFile,
            BigDecimal heightCm,
            BigDecimal weightKg,
            String seqAccount,
            String gender
    ){
        try{
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            builder.part("file", imageFile.getResource())
                    .filename(imageFile.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(
                            imageFile.getContentType() != null
                            ? imageFile.getContentType()
                            : MediaType.IMAGE_JPEG_VALUE
                    ));

            builder.part("height", heightCm.toPlainString());
            builder.part("weight", weightKg.toPlainString());
            builder.part("seq_account", seqAccount);
            builder.part("gender", gender);

            Sam3dBodyApiResponse response = webClient.post()
                    .uri("/analyze-body")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Sam3dBodyApiResponse.class)
                    .block();

            if(response == null){
                throw new IllegalArgumentException("Sam3dBodyApiResponse is null");
            }

            log.info("✅ SAM3D 분석 완료: success={}, message={}",
                    response.isSuccess(), response.getMessage());

            return response;
        }catch (Exception e){
            log.error("❌ SAM 3D Body 분석 API 호출 중 에러", e);
            throw new RuntimeException("SAM 3D Body 분석 API 호출 실패", e);
        }
    }
}
