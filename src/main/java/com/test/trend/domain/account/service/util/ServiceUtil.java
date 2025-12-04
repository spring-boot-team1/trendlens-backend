package com.test.trend.domain.account.service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
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

    public LocalDate parseAndValidateBirthday(String birthday) {

        // 1. null 체크 & 길이 체크
        if (birthday == null || birthday.length() != 8) {
            throw new IllegalArgumentException("생년월일은 YYYYMMDD 형식의 8자리 여야 합니다.");
        }

        // 2. 숫자만 있는지 검사
        if (!birthday.matches("\\d{8}")) {
            throw new IllegalArgumentException("생년월일은 숫자 8자리여야 합니다.");
        }

        // 3. 연/월/일 분리
        int year = Integer.parseInt(birthday.substring(0, 4));
        int month = Integer.parseInt(birthday.substring(4, 6));
        int day = Integer.parseInt(birthday.substring(6, 8));

        // 4. 실제 존재하는 날짜인지 체크 (LocalDate가 알아서 검증)
        try {
            System.out.println(LocalDate.of(year, month, day));
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("유효하지 않은 생년월일입니다.");
        }
    }


    public String parseandValidatePhoneNum(String phonenum) {

        if (phonenum == null || phonenum.isBlank()) {
            throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
        }

        // 하이픈 모두 제거 (01012345678 형태 만들기)
        String digits = phonenum.replaceAll("[^0-9]", "");

        // 11자리인지 확인 (010xxxxxxxx)
        if (!digits.matches("^01[016789]\\d{7,8}$")) {
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다.");
        }

        // 010-1234-5678 형식으로 정규화
        String first = digits.substring(0, 3);
        String middle = digits.length() == 10 ? digits.substring(3, 6) : digits.substring(3, 7);
        String last = digits.substring(digits.length() - 4);

        return first + "-" + middle + "-" + last;
    }
}
