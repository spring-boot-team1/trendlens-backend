package com.test.trend.domain.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.account.dto.RegisterRequestDTO;
import com.test.trend.domain.account.service.AccountService;
import com.test.trend.domain.account.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    @Hidden
//    @PostMapping(value ="/api/v1/signup",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> signup(
//            @Valid @RequestPart("dto")RegisterRequestDTO dto,
//            @RequestPart(value = "profilepic", required = false) MultipartFile image) {
//        System.out.println("UserController.register() >>>>> " + dto);
//        accountService.signup(dto, image); //비즈니스 로직은 Service객체에 위임
//        return ResponseEntity.ok().build(); // 200 OK
//    }


//    @PostMapping(value ="/api/v2/signup",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> signup2(
//            @RequestPart("dto")String dtoJson,
//            @RequestPart(value = "profilepic", required = false) MultipartFile image) throws JsonProcessingException {
//        System.out.println("UserController.register() >>>>> " + dtoJson);
//
//        RegisterRequestDTO dto = objectMapper.readValue(dtoJson, RegisterRequestDTO.class);
//        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validate(dto);
//        if (!violations.isEmpty()) {
//            String message = violations.iterator().next().getMessage();
//            throw new IllegalArgumentException(message);
//        }
//
//        accountService.signup(dto, image); //비즈니스 로직은 Service객체에 위임
//        return ResponseEntity.ok().build(); // 200 OK
//    }

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
    public ResponseEntity<?> logout() {

        return null;
    }

}