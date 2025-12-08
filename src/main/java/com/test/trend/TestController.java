package com.test.trend;

import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.mapper.SampleMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

//테스트용 rest controller
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DataSource dataSource;
    private final SampleMapper mapper;

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

    @GetMapping("/auth-check")
    public String authCheck(Authentication authentication) {
        CustomAccountDetails user = (CustomAccountDetails) authentication.getPrincipal();
        return "TestController >>>>> 로그인됨: email=" + user.getEmail() + ", nickname=" + user.getNickname() + ", role=" + user.getRole();
    }
}
