package com.test.trend.domain.user.controller;

import com.test.trend.domain.user.dto.RegisterRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @GetMapping("/api/v1/login")
    public String login() {
        return "UserController >>>>> /login";
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(@Valid @RequestPart("dto")RegisterRequestDTO dto, @RequestPart(value = "profilepic", required = false)MultipartFile image) {

        return null; //결과(HTTPSTATUS)
    }

}