package com.test.trend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 관련 설정을 담당하는 설정 클래스.
 * 단일 Redis 인스턴스(Standalone)에 대해 접속 설정을 구성하고, RedisTemplate 사용을 위한 Bean들을 등록한다.
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    /**
     * Redis 연결 Factory 객체 생성
     * Spring Data Redis가 Redis 서버에 연결할 때 사용하는 핵심 Factory 객체이다.
     * Lettuce 기반 클라이언트를 사용하며, Standalone 구조의 Redis 환경에 필요한 접속 정보(호스트, 포트, 비밀번호)를 설정한다.
     * @return LettuceConnectionFactory, Redis 서버 연결 담당
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 접속 정보 설정
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);

        // 비밀번호가 있을 경우에만 설정
        if (redisPassword != null && !redisPassword.isBlank()) {
            config.setPassword(redisPassword);
        }

        // LettuceConnectionFactory에 config 주입
        return new LettuceConnectionFactory(config);
    }

    /**
     * 문자열 기반의 key-value 데이터를 Redis에 저장하거나 조회할 때 사용하는 템플릿.
     * RefreshToken, 인증 관련 토큰, 간단한 문자열 캐시 등을 처리하는 데 적합하다.
     * Redis 연산을 편리하게 사용할 수 있다.
     * @param connectionFactory Redis 연결 객체
     * @return StringRedisTemplate - 문자열 전용 RedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
