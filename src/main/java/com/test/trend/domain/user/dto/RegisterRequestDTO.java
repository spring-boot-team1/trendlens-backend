package com.test.trend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank //null체크, 빈문자열체크, " "체크
    private String email;

    @NotBlank
    private String password;

    private String username;
    private String nickname;
    private String phonenum;
    private String birthday;
}
