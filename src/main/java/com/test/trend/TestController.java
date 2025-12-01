package com.test.trend;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

//DB 연결 확인용 rest controller
// 접속 URL: localhost:8080/trend/db-test
@RestController
@RequiredArgsConstructor
public class TestController {
    private final DataSource dataSource;
    @GetMapping("/db-test")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "DB Connected! - " + conn.getMetaData().getURL();
        } catch (Exception e) {
            e.printStackTrace();
            return "DB Connection Failed: " + e.getMessage();
        }
    }
}
