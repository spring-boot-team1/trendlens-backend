package com.test.trend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RedisConnectionTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void redisConnectionAndAuthTest() {
        String key = "test:redis";
        String value = "hello";

        // SET
        redisTemplate.opsForValue().set(key, value);

        // GET
        String result = redisTemplate.opsForValue().get(key);

        // DELETE
        redisTemplate.delete(key);

        // Assertions (값 검증)
        assertThat(result).isEqualTo("hello");

        System.out.println("Redis 테스트 성공: " + result);
    }
}
