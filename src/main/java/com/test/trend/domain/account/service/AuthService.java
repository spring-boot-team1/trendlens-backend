package com.test.trend.domain.account.service;

import com.test.trend.auth.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService; //Redis 저장, 조회, 삭제 담당 서비스 레이어
    private final JWTUtil jwtUtil;
    private final HttpServletResponse response;

    public String reissue(String refreshToken, HttpServletResponse response) {
        // 쿠키에 refresh token 존재 확인
        if (refreshToken == null) {
            throw new RuntimeException("No refresh token in cookie");
        }
        // refresh token 유효성 검사(jwtUtil에 구현)
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }
        Long seqAccount = jwtUtil.getUserIdFromRefresh(refreshToken);

        return null;
    }
}
