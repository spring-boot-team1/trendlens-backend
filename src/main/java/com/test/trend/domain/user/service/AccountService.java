package com.test.trend.domain.user.service;

import com.test.trend.domain.user.dto.RegisterRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AccountService {

    public void register(@Valid RegisterRequestDTO dto, MultipartFile image) {
    }
}
