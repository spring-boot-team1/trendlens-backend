package com.test.trend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) throws Exception {
        //CORS 설정
        http.cors(auth -> auth.configurationSource(corsConfigurationSource()));

        //CSRF 비활성
        http.csrf(auth -> auth.disable());
        //폼 로그인 비활성
        http.formLogin(auth -> auth.disable());
        // 로그아웃 비활성
        http.logout(auth -> auth.disable());
        // 기본 인증 비활성
        http.httpBasic(auth -> auth.disable());
        //세션 인증 방식 비활성, JWT 사용 방식으로 변경
        http.sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //허가 URL
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                .anyRequest().permitAll());
        return http.build();
    }

    //CORS 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); //클라이언트 주소
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
