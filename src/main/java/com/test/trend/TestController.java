package com.test.trend;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

//테스트용 rest controller
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DataSource dataSource;

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

    // Swagger 테스트 - 접속 URL: http://localhost:8080/trend/swagger-ui/index.html
    @Hidden
    @GetMapping("/swagger-test")
    public String swaggerTest() {
        return "swagger-ok";
    }
}
