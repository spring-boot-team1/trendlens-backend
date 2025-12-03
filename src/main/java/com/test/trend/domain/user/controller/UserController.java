package com.test.trend.domain.user.controller;

import com.test.trend.domain.user.dto.AccountDetailDTO;
import com.test.trend.domain.user.dto.RegisterRequestDTO;
import com.test.trend.domain.user.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final AccountService accountService;

    @GetMapping("/api/v1/login")
    public String login() {
        return "UserController >>>>> /login";
    }

    /**
     * 회원 가입 요청 처리
     * @param dto 폼데이터(이미지 제외)
     * @param image 프로필사진
     * @return
     */
    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(
            @Valid @RequestPart("dto")RegisterRequestDTO dto,
            @RequestPart(value = "profilepic", required = false) MultipartFile image) {
        System.out.println("UserController.register() >>>>> " + dto);
        accountService.register(dto, image); //비즈니스 로직은 Service객체에 위임
        return null; //결과(HTTPSTATUS)
    }

}