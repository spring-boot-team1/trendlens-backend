package com.test.trend.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/v1/login")
    public String login() {
        return "UserController >>>>> /login";
    }

    @PostMapping("/api/v1/register")
    public String register() {
        return "UserController >>>>> /register";
    }

}