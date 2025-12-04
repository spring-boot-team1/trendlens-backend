package com.test.trend.domain.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.account.dto.RegisterRequestDTO;
import com.test.trend.domain.account.service.AccountService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @GetMapping("/api/v1/login")
    public String login() {
        return "UserController >>>>> /login";
    }

    /**
     * 회원 가입 요청 처리(작동안함)
     * 계속 application/octet-stream으로 인식됨
     * @param dto 폼데이터(이미지 제외)
     * @param image 프로필사진
     * @return
     */
    @Hidden
    @PostMapping(value ="/api/v1/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @Valid @RequestPart("dto")RegisterRequestDTO dto,
            @RequestPart(value = "profilepic", required = false) MultipartFile image) {
        System.out.println("UserController.register() >>>>> " + dto);
        accountService.signup(dto, image); //비즈니스 로직은 Service객체에 위임
        return ResponseEntity.ok().build(); // 200 OK
    }

    /**
     * 회원 가입 요청 처리
     * @param dto 폼데이터(이미지 제외)
     * @param image 프로필사진
     * @return
     */
    @PostMapping(value ="/api/v2/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup2(
            @Valid @RequestPart("dto")String dtoJson,
            @RequestPart(value = "profilepic", required = false) MultipartFile image) throws JsonProcessingException {
        System.out.println("UserController.register() >>>>> " + dtoJson);

        RegisterRequestDTO dto = objectMapper.readValue(dtoJson, RegisterRequestDTO.class);
        accountService.signup(dto, image); //비즈니스 로직은 Service객체에 위임
        return ResponseEntity.ok().build(); // 200 OK
    }

}