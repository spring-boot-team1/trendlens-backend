package com.test.trend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.query.KeysetScrollDelegate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//1. JWTUtil 구현
/**
 * JWT 토큰 생성 관련 메서드들을 담은 클래스
 */
@Component
public class JWTUtil {
    private final SecretKey secretKey;
    private final Long accessExpiredMs;
    @Getter //나중에 SecurityConfig에서 Getter 사용 필요해서 작성
    private final Long refreshExpiredMs;

    /**
     * JWTUtil의 생성자
     * @param secretKey 서명
     * @param accessExpiredMs 액세스 토큰 만료 시간
     * @param refreshExpiredMs 리프레시 토큰 만료 시간
     */
    public JWTUtil(
            @Value("${spring.jwt.secret}") String secretKey,
            @Value("${spring.jwt.access-token-expiration}") Long accessExpiredMs,
            @Value("${spring.jwt.refresh-token-expiration}") Long refreshExpiredMs) {

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); //Creates a new SecretKey instance for use with HMAC-SHA algorithms based on the specified key byte array.
        this.accessExpiredMs = accessExpiredMs; //액세스 토큰 만료 시간
        this.refreshExpiredMs = refreshExpiredMs; //리프레시 토큰 만료 시간
    }

    /**
     * JWT 문자열을 생성하는 메서드
     * 인증 과정을 거친 후 생성된 JWT 문자열은 클라이언트에게 전달된다.
     * @param email 사용자 이메일
     * @param nickname 사용자 닉네임
     * @param role 사용자 권한
     * @param expiredMs 토큰 만료 시간
     * @return JWT 문자열(header.payload.signature 형태)
     */
    public String createJWT(String email, String nickname, String role, Long expiredMs){
        /* 
        claim(): 토큰의 페이로드에 사용자 정보를 저장
        issuedAt()/expiration(): 토큰 생성/만료 시간
        signWith(): 서명(위변조 방지)
        compact(): header.payload.signature 형태의 최종 JWT 문자열을 생성
        */
        return Jwts.builder()
                .claim("email", email)
                .claim("nickname", nickname)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 액세스 토큰 생성 메서드
     * @param email 사용자 이메일
     * @param nickname 사용자 닉네임
     * @param role 사용자 권한
     * @return 액세스 토큰 JWT 문자열
     */
    public String createAccessToken(String email, String nickname, String role) {
        return createJWT(email, nickname, role, accessExpiredMs);
    }

    /**
     * 리프레시 토큰 생성 메서드
     * @param email 사용자 이메일
     * @param nickname 사용자 닉네임
     * @param role 사용자 권한
     * @return 리프레시 토큰 JWT 문자열
     */
    public String createRefreshToken(String email, String nickname, String role) {
        return createJWT(email, nickname, role, refreshExpiredMs);
    }

    /**
     * 토큰 검증을 하고 JWT 문자열을 파싱하여 정보를 추출하는 메서드
     * @param token JWT 문자열
     * @return Claims
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Claims에서 사용자 이메일을 꺼냄
     * @param token JWT 문자열
     * @return 사용자 이메일
     */
    public String getEmail (String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * Claims에서 사용자 권한을 꺼냄
     * @param token JWT 문자열
     * @return 사용자 권한
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Claims에서 사용자 닉네임을 꺼냄
     * @param token JWT 문자열
     * @return 사용자 닉네임
     */
    public String getNickname(String token) {
        return getClaims(token).get("nickname", String.class);
    }

    /**
     * 토큰 만료 시간을 확인
     * @param token JWT 문자열
     * @return 만료 여부 Boolean
     */
    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

}
