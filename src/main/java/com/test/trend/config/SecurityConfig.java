package com.test.trend.config;

import com.test.trend.auth.JWTFilter;
import com.test.trend.auth.JWTUtil;
import com.test.trend.auth.LoginFilter;
import com.test.trend.domain.account.repository.AccountDetailRepository;
import com.test.trend.domain.account.repository.AccountRepository;
import com.test.trend.domain.account.service.RedisService;
import com.test.trend.domain.account.service.util.ClaimsBuilderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //주입(필터 등록 시 사용)
    private final AuthenticationConfiguration configuration;
    private final JWTUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final AccountDetailRepository accountDetailRepository;
    private final RedisService redisService;
    private final ClaimsBuilderUtil claimsBuilderUtil;

    //OAuth2가 아닐 때 사용하기 위한 BCryptPasswordEncoder
    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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

        /*
            허가URL 샘플
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login/**", "/join/**", "/joinok/**").permitAll()
                    .requestMatchers("/member").hasAnyRole("MEMBER", "ADMIN")
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated()
            );
        */
        //허가 URL
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()     // 로그인은 항상 허용
                .requestMatchers("/auth-check", "/api/v1/logout").authenticated() //인증 필요 테스트 페이지
//                .requestMatchers("/trend/**").permitAll()
                .anyRequest().permitAll());

        //JWTFilter 등록하기
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //LoginFilter 등록하기
        http.addFilterAt(new LoginFilter(
                accountRepository,
                accountDetailRepository,
                manager(configuration),
                jwtUtil,
                redisService,
                claimsBuilderUtil
        ), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    /**
     * AuthenticationManager - JWT 인증 관리
     * @param configuration AuthenticationConfiguration
     * @return getAuthenticationManager()
     * @throws Exception exception
     */
    @Bean
    AuthenticationManager manager(AuthenticationConfiguration configuration) throws Exception {
        // AuthenticationManager - JWT 인증 관리
        // 사용자가 로그인 시도 -> 실제로 ID와 PW가 일치하는지 검증
        // loadUserByUsername 관여
        // 직접 생성 이유 -> 폼 인증을 사용하지 않아서
        return configuration.getAuthenticationManager();
    }

    //CORS 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:5173"); //클라이언트 주소(패턴화, addAllowedOrigin보다 조금 더 유연)
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
