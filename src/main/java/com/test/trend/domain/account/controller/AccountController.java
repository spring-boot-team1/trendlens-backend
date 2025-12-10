package com.test.trend.domain.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.account.dto.RegisterRequestDTO;
import com.test.trend.domain.account.service.AccountService;
import com.test.trend.domain.account.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final AuthService authService;

    @Hidden
    @GetMapping("/api/v1/login")
    public String login() {
        return "UserController >>>>> /login";
    }

    /**
     *
     * @param registerRequestDTO 회원가입 요청
     * @return 200 OK
     */
    @PostMapping(value ="/api/v3/signup")
    public ResponseEntity<?> signup3(@RequestBody RegisterRequestDTO registerRequestDTO){
        System.out.println("UserController.register() >>>>> " + registerRequestDTO);
        accountService.signup(registerRequestDTO); //비즈니스 로직은 Service객체에 위임
        return ResponseEntity.ok().build(); // 200 OK
    }

    @PostMapping("/api/v1/reissue")
    public ResponseEntity<?> reissue(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        String newAccessToken = authService.reissue(refreshToken, response);
        return ResponseEntity.ok().header("Authorization", "Bearer " + newAccessToken).build();
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Long seqAccount = ((CustomAccountDetails) authentication.getPrincipal()).getSeqAccount();
        accountService.logout(seqAccount, response);
        return ResponseEntity.ok(Map.of("message", "logout success"));
    }

}