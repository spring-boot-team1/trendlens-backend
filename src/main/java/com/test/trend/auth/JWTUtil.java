package com.test.trend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

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
     * @param claims JWT Claim들을 담은 Map
     * @param expiredMs 토큰 만료 시간
     * @return JWT 문자열(header.payload.signature 형태)
     */
    public String createJWTString(Map<String, Object> claims, Long expiredMs){
        /* 
        claim(): 토큰의 페이로드에 사용자 정보를 저장
        issuedAt()/expiration(): 토큰 생성/만료 시간
        signWith(): 서명(위변조 방지)
        compact(): header.payload.signature 형태의 최종 JWT 문자열을 생성
        */
        JwtBuilder builder = Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey);

        // Map에 있는 모든 claim 자동 추가
        claims.forEach(builder::claim);

        return builder.compact();
    }

    /**
     * 액세스 토큰 생성 메서드. claims를 map에 담아 createJWT() 메서드를 반환한다.
     * @param claims claim들을 담은 Map
     * @return createJWT() 메서드
     */
    public String createAccessToken(Map<String, Object> claims) {
        return createJWTString(claims, accessExpiredMs);
    }

    /**
     * 리프레시 토큰 생성 메서드. claims를 map에 담아 createJWT() 메서드를 반환한다.
     * @param claims claim들을 담은 Map
     * @return createJWT() 메서드
     */
    public String createRefreshToken(Map<String, Object> claims) {
        return createJWTString(claims, refreshExpiredMs);
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
     * Claim 꺼내는 getter(통합버전)
     * @param <T> 반환 타입 (Claim의 자료형)
     * @param token JWT 문자열
     * @param key 꺼낼 값의 Key 이름
     * @param type 꺼낼 값의 자료형 클래스
     * @return 토큰에서 꺼낸 사용자 정보
     */
    public <T> T getClaim(String token, String key, Class<T> type) {
        return getClaims(token).get(key, type);
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
     * Claims에서 사용자 PK를 꺼냄
     * @param token JWT 문자열
     * @return 사용자 닉네임
     */
    public Long getSeqAccount(String token) {
        return getClaims(token).get("seqAccount", Long.class);
    }

    /**
     * 토큰 만료 시간을 확인
     * @param token JWT 문자열
     * @return 만료 여부 Boolean
     */
    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * JWT 토큰의 유효성 검사
     * @param refreshToken 리프레시토큰 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            //만료 여부 확인
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * RefreshToken에서 사용자 PK(seqAccount)를 추출
     * @param refreshToken 리프레시 토큰
     * @return seqAccount(Long)
     */
    public Long getUserIdFromRefresh(String refreshToken) {
        //refreshToken에서 seqAccount 꺼내오기
        return getClaims(refreshToken).get("seqAccount", Long.class);
    }
}
