package com.test.trend.domain.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 회원 가입 요청 시 사용하는 DTO(사용자 입력 데이터), Request용 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @Email(message = "올바른 이메일 형식이 아닙니다.") //유효성 검사
    @NotBlank(message="이메일은 필수 입력값입니다.") //null체크, 빈문자열체크, " "체크
    private String email;

    @NotBlank
    private String password;

    private String username;
    private String nickname;
    private String phonenum;
    private String birthday;
}
