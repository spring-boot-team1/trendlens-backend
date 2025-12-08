package com.test.trend.auth;

// 3. JWTFilter(Authorization)
// 현재 요청(접속)한 사용자가 유효한 JWT 토큰을 가지고 있는지 검사
// 이 사용자를 인증된 사용자로 인식하게 만든다.

import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.entity.AccountDetail;
import com.test.trend.domain.account.mapper.AccountMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    //주입
    private final JWTUtil jwtUtil;
//    private final AccountMapper accountMapper;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request 요청
     * @param response 응답
     * @param filterChain 필터
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 로그인 요청은 JWT 검증 대상이 아님
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 유무 체크 + 접두어(Bearer) 유무 체크
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            //System.out.println("JWTFilter >>>>> JWT Token Invalid");
            //인증되지 않은 사용자 -> 익명 처리
            filterChain.doFilter(request, response);
            return;
        }

        //정상적 토큰
        String token = authorization.split(" ")[1]; //Bearer 접두어 제거
        //token에서 정보를 추출 -> Spring Security 인증 객체 생성
        String email = null;
        String nickname = null;
        String role = null;
        try {
            email = jwtUtil.getEmail(token);
            nickname = jwtUtil.getNickname(token);
            role = jwtUtil.getRole(token);
        } catch (Exception e) {
            System.out.println("JWTFilter >>>>> Unauthorized");
            //response.setStatus(401); //401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            return;
        }
        
        
        //여기까지 넘어오면 토큰 정상 + payload들 확보 => Security 처리
        // a. CustomAccountDetails(+Account) 인증 객체
        // b. 시큐리티에 적용

        CustomAccountDetails customAccountDetails = CustomAccountDetails.builder()
                .email(email)
                .nickname(nickname)
                .role(role)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(customAccountDetails, null, customAccountDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
