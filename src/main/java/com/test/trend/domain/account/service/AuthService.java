package com.test.trend.domain.account.service;

import com.test.trend.auth.JWTUtil;
import com.test.trend.domain.account.service.util.ClaimsBuilderUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService; //Redis 저장, 조회, 삭제 담당 서비스 레이어
    private final JWTUtil jwtUtil;
    private final ClaimsBuilderUtil claimsBuilderUtil;

    /**
     * RefreshToken으로 AccessToken 재발급하는 서비스 계층
     * @param refreshToken 리프레시 토큰
     * @param response 응답
     * @return 새 액세스 토큰
     */
    public String reissue(String refreshToken, HttpServletResponse response) {
        System.out.println("reissue() 호출됨, refreshToken=" + refreshToken);

        // 쿠키에 refresh token 존재 확인
        if (refreshToken == null) {
            throw new RuntimeException("No refresh token in cookie");
        }
        // refresh token 유효성 검사(jwtUtil에 구현)
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }
        Long seqAccount = jwtUtil.getUserIdFromRefresh(refreshToken);
        String redisToken = redisService.getRefreshToken(seqAccount);

        //액세스토큰 재발급
        Map<String, Object> accessClaims = claimsBuilderUtil.buildAccessClaims(seqAccount);
        String newAccessToken = jwtUtil.createAccessToken(accessClaims);

        //리프레시토큰 재발급
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("seqAccount", seqAccount);
        String newRefreshToken = jwtUtil.createRefreshToken(refreshClaims);
        //Redis에서 새 refreshToken 갱신
        redisService.saveRefreshToken(seqAccount, newRefreshToken, jwtUtil.getRefreshExpiredMs());
        
        //쿠키로 내리기
        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwtUtil.getRefreshExpiredMs()/1000));
        response.addCookie(cookie);
        return newAccessToken;
    }
}
