package com.test.trend.auth;

import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.account.repository.AccountDetailRepository;
import com.test.trend.domain.account.repository.AccountRepository;
import com.test.trend.domain.account.service.RedisService;
import com.test.trend.domain.account.service.util.ClaimsBuilderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//2. LoginFilter 구현

/**
 * 사용자가 로그인 URL에 접속해 ID/PW를 전송하면 개입하는 LoginFilter 클래스. 
 * 개입하여 AuthenticationManager에게 인증을 위임한 뒤, 인증에 성공하면 토큰을 발급한다.
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AccountRepository accountRepository;
    private final AccountDetailRepository accountDetailRepository;
    private final AuthenticationManager authManager; //인증 담당
    private final JWTUtil jwtUtil; //토큰 발행 담당
    private final RedisService redisService;
    private final ClaimsBuilderUtil claimsBuilderUtil;

    // Redis에 리프레시 토큰 저장할 때에는 어떻게 구현하는지??
    //private final RefreshTokenRepository refreshTokenRepository;
    //private final Long refreshExpiredMs;

    /**
     * LoginFilter 생성자
     * @param accountRepository 쿼리 담당
     * @param accountDetailRepository 쿼리 담당
     * @param authManager 인증 담당
     * @param jwtUtil 토큰 발행 담당
     */
    public LoginFilter(
            AccountRepository accountRepository,
            AccountDetailRepository accountDetailRepository,
            AuthenticationManager authManager,
            JWTUtil jwtUtil,
            RedisService redisService,
            ClaimsBuilderUtil claimsBuilderUtil) {
        this.accountRepository = accountRepository;
        this.accountDetailRepository = accountDetailRepository;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.claimsBuilderUtil = claimsBuilderUtil;
    }

    /**
     * 사용자가 로그인 시도 시(/login) 호출되는 메서드
     * @param request 요청
     * @param response 응답
     * @return 인증 요청
     * @throws AuthenticationException 인증 예외
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //사용자가 입력한 ID와 비밀번호
        String email = obtainUsername(request); //사용자 이메일을 읽어옴
        String password = obtainPassword(request); //사용자 비밀번호를 읽어옴

        System.out.println("LoginFilter username >>>>> " + email);
        System.out.println("LoginFilter password >>>>> " + password);

        //사용자 입력 정보를 DTO로 포장해서 authManager에게 전달
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //authManager에게 인증 요청
        return authManager.authenticate(authenticationToken);
    }

    /**
     * attemptAuthentication()의 결과 중 인증을 성공했을 때(ID/PW)가 올바를 때 호출되는 메서드.
     * JWT Access Token과 Refresh Token을 생성 및 발급한다.
     * @param request 요청
     * @param response 응답
     * @param chain Filter
     * @param authResult 인증 결과
     * @throws IOException Exception
     * @throws ServletException Exception
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //Authentication -> 인증된 사용자 객체
        //인증에 성공하면 JWT Access Token 발급
        System.out.println("LoginFilter >>>>> Login Success");

        CustomAccountDetails customAccountDetails = (CustomAccountDetails) authResult.getPrincipal();
        //아이디(이메일) 꺼내오기
        String email = customAccountDetails.getUsername();
        //ROLE 꺼내오기
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        Long seqAccount = customAccountDetails.getSeqAccount();
        //닉네임 꺼내오기
        //String nickname = customAccountDetails.getNickname();

        Map<String, Object> accessClaims = claimsBuilderUtil.buildAccessClaims(seqAccount);
        String accessToken = jwtUtil.createAccessToken(accessClaims);

        // 액세스토큰을 클라이언트에게 전달
        response.setHeader("Authorization", "Bearer " + accessToken);

        //4. 로그인 성공 시 RefreshToken 발급 & 저장
        //JWT Refresh Token 생성
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("seqAccount", customAccountDetails.getSeqAccount());
        String refreshToken = jwtUtil.createRefreshToken(refreshClaims);
        //refreshtoken을 Redis에 저장
        redisService.saveRefreshToken(seqAccount, refreshToken, jwtUtil.getRefreshExpiredMs());

        // 리프레시토큰을 클라이언트에게 전달
        //헤더(임시)
        //response.setHeader("RefreshToken", refreshToken);

        // HttpOnly 쿠키를 생성(보안 문제)
//        Cookie cookie = new Cookie("refreshToken", refreshToken);
//        cookie.setMaxAge((int)(jwtUtil.getRefreshExpiredMs()/1000)); //단위: 초
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
        //cookie.setSecure(true); //Https에서 사용한 쿠키값 암호화 옵션, 나중에 배포시 주석해제예정
//        response.addCookie(cookie);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)              // ★ HTTPS 전용
                .sameSite("None")          // ★ 프론트/백엔드 분리
                .path("/")
                .maxAge(Duration.ofMillis(jwtUtil.getRefreshExpiredMs()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    /**
     * attemptAuthentication()의 결과 중 인증 실패시 예외 처리를 위한 메서드
     * @param request 요청
     * @param response 응답
     * @param failed 인증 실패
     * @throws IOException Exception
     * @throws ServletException Exception
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("LoginFilter >>>>> Login Failed");
        super.unsuccessfulAuthentication(request, response, failed);
    }
}