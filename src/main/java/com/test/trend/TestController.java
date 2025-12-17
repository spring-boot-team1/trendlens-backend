package com.test.trend;

import com.test.trend.auth.JWTUtil;
import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.mapper.SampleMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

//테스트용 rest controller
@Hidden
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DataSource dataSource;
    private final SampleMapper mapper;
    private final JWTUtil jwtUtil;

    //DB 테스트 접속 URL: localhost:8080/trend/db-test
    @GetMapping("/db-test")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "DB Connected! - " + conn.getMetaData().getURL();
        } catch (Exception e) {
            e.printStackTrace();
            return "DB Connection Failed: " + e.getMessage();
        }
    }

    @GetMapping("/time")
    public String time() {
        return mapper.time();
    }

    // Swagger 테스트 - 접속 URL: http://localhost:8080/trend/swagger-ui/index.html
    @Hidden
    @GetMapping("/swagger-test")
    public String swaggerTest() {
        return "swagger-ok";
    }

    /**
     * 로그인 확인용 테스트 컨트롤러
     * @param authentication authentication
     * @param header HTTP 헤더
     * @return 로그인 사용자의 claims
     */
    @GetMapping("/auth-check")
    public String authCheck(Authentication authentication, @RequestHeader("Authorization") String header) {
        CustomAccountDetails user = (CustomAccountDetails) authentication.getPrincipal();
        String token = header.replace("Bearer ", "");

        // 추가 Claim 파싱
        //Long seqAccount = jwtUtil.getClaim(token, "seqAccount", Long.class);
        String provider = jwtUtil.getClaim(token, "provider", String.class);
        String providerId = jwtUtil.getClaim(token, "providerId", String.class);
        Long seqAccountDetail = jwtUtil.getClaim(token, "seqAccountDetail", Long.class);
        String username = jwtUtil.getClaim(token, "username", String.class);
        String profilepic = jwtUtil.getClaim(token, "profilepic", String.class);
        String nickname = jwtUtil.getClaim(token, "nickname", String.class);

        return """
        TestController >>>>> 로그인됨:
        email = %s
        nickname = %s
        role = %s
        seqAccount = %d
        provider = %s
        providerId = %s
        seqAccountDetail = %d
        username = %s
        profilepic = %s""".formatted(
                user.getEmail(),
                nickname,
                user.getRole(),
                user.getSeqAccount(),
                provider,
                providerId,
                seqAccountDetail,
                username,
                profilepic
        );
    }
}
