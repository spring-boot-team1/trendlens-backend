package com.test.trend.domain.account.service.util;

import com.test.trend.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;
import java.util.UUID;

@Component
public class ServiceUtil {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 이미지 크기 검사
     * @param image 첨부파일
     */
    public void validateFileSize(MultipartFile image) {
        if (image == null || image.isEmpty()) return;

        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("업로드 가능한 파일 크기는 최대 10MB 입니다.");
        }
    }

    /**
     * 이미지파일 확장자 검사
     * @param image 첨부파일
     */
    public void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return; // 이미지가 필수 입력이 아니므로 통과
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("유효하지 않은 이미지 파일명입니다.");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_IMAGE_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용되지 않은 이미지 확장자입니다. (jpg, jpeg, png, gif, webp만 가능)");
        }
    }

    /**
     * 프로필 사진 저장
     * @param image 첨부파일
     * @return DB에 저장할 파일명
     */
    public String saveProfileImage(MultipartFile image) {
        try {
            // 1. 폴더 유무 체크 후 없으면 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2. 확장자 추출
            String originalFileName = image.getOriginalFilename();
            String ext = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                ext = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            // 3. UUID 기반 파일명 생성
            String filename = UUID.randomUUID().toString() + ext;
            // 4. 실제 저장할 파일객체 생성
            File dest = new File(uploadDir + File.separator + filename);
            // 5. 파일 저장
            image.transferTo(dest);
            // 6. DB에 저장할 파일명만 반환
            return filename;

        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }
    }


}
