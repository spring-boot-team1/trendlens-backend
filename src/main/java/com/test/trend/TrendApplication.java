package com.test.trend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //스케줄러 적용
@SpringBootApplication
public class TrendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrendApplication.class, args);
    }

}
