package com.test.trend.domain.analyze.service;

import com.test.trend.domain.analyze.model.StoredBodyPhotoDTO;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3BodyPhotoStorage {
/**
        * 사용자의 몸 사진을 S3에 업로드하는 서비스.

            * - 업로드 경로 컨벤션:
            *   uploads/body-photo/{username}/{uuid}.{ext}

        예) uploads/analyze/body-photo/jaeman/550e8400-e29b-41d4-a716-446655440000.jpg
        - 반환값:S3 Object Key (위의 예시처럼 "uploads/..." 부터의 문자열)
         -> 나중에 이 key를 가지고 CloudFront URL 만들든, 프론트에 보내든 활용하면 됨.
 **/

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Operations s3Operations;
    private static final String ROOT_PATH = "uploads/analyze/body-photo";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    /**
     * @param username 업로드한 사용자 아이디
     * @param file 업로드된 멀티파트파일(이미지)
     * @return      저장결과 DTO
     */
    public StoredBodyPhotoDTO uploadBodyPhoto(String username, MultipartFile file){
        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드 된 파일이 없습니다.");
        }

        String ext = extractExtension(file);
        validateExtension(ext);

        String uuid = UUID.randomUUID().toString();
        String key = String.format("%s/%s/%s.%s", ROOT_PATH, username, uuid, ext);

        String contentType = resolveContentType(file, ext);

        try {
            S3Resource resource = s3Operations.upload(
                    bucketName,
                    key,
                    file.getInputStream()
            );

            URL url = resource.getURL();
            log.info("body photo upload url : {}, key={}, bucket={}", url, key, bucketName);

            return new StoredBodyPhotoDTO(
                    bucketName,
                    key,
                    url.toString(),
                    ext,
                    contentType
            );

        } catch (IOException e) {
            log.error("Failed to upload body photo to S3", e);
            throw new RuntimeException("이미지 업로드 중 오류가 발생");
        }
    }



    /**
     * 파일 이름에서 확장자(ext)만 추출한다.
     *
     * 예:
     *  - "photo.jpg"     -> "jpg"
     *  - "my.file.png"   -> "png"
     *  - "noextension"   -> "" (빈 문자열)
     *
     * @param file 원본 파일 이름
     * @return 확장자(마지막 점 기준). 없으면 빈 문자열 반환.
     */
    private String extractExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("파일 이름을 알 수 없습니다.");
        }

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == originalName.length() - 1) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다. 파일명: " + originalName);
        }

        return originalName.substring(dotIndex + 1).toLowerCase();  // "PNG" → "png"
    }

    /**
     * 허용된 확장자인지 체크. (png / jpg / jpeg)
     */
    private void validateExtension(String ext) {
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (허용: png, jpg, jpeg) / 요청 ext = " + ext);
        }
    }

    /**
     * Content-Type 결정 로직
     * 1) MultipartFile 에 contentType 이 있으면 그걸 쓰고
     * 2) 없으면 확장자로 추론
     */
    private String resolveContentType(MultipartFile file, String ext) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }

        // 확장자로 fallback
        return switch (ext) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            default -> "application/octet-stream"; // 여기까지 올 일은 사실상 없음, 방어용
        };
    }

}