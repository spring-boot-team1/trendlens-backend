package com.test.trend.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
/**
 * refresh token에 대한 조회, 저장, 삭제 메서드를 담은 서비스 계층
 */
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    /**
     * RefreshToken 저장
     * @param seqAccount Account 테이블 PK
     * @param refreshToken 리프레시토큰
     * @param expireTimeMs 만료시간
     */
    public void saveRefreshToken(Long seqAccount, String refreshToken, long expireTimeMs) {
        String key = "RT:" + seqAccount; //key 규칙 -> "RT:{사용자PK}"
        redisTemplate.opsForValue().set(
            key,
            refreshToken,
            Duration.ofMillis(expireTimeMs)
        );
    }

    /**
     * RefreshToken 조회
     * @param seqAccount Account 테이블 PK
     * @return 해당 사용자의 RefreshToken
     */
    public String getRefreshToken(Long seqAccount) {
        return redisTemplate.opsForValue().get("RT:" + seqAccount);
    }

    /**
     * RefreshToken 삭제
     * @param seqAccount Account 테이블 PK
     * @return 삭제 유무
     */
    public boolean deleteRefreshToken(Long seqAccount) {
        String key = "RT:" + seqAccount;
        Boolean exists = redisTemplate.hasKey(key);
        if (exists) {
            return redisTemplate.delete(key);  // 삭제 성공 여부
        }
        return false; // 존재하지 않음
    }

}
