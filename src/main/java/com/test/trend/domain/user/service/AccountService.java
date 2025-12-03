package com.test.trend.domain.user.service;

import com.test.trend.domain.user.dto.RegisterRequestDTO;
import com.test.trend.domain.user.mapper.AccountDetailMapper;
import com.test.trend.domain.user.mapper.AccountMapper;
import com.test.trend.domain.user.repository.AccountDetailRepository;
import com.test.trend.domain.user.repository.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AccountService {
    //주입
    private final BCryptPasswordEncoder passwordEncoder; //SecurityConfig에 Bean 선언 필요
    private final AccountRepository accountRepository;
    private final AccountDetailRepository accountDetailRepository;
    private final AccountMapper accountMapper;
    private final AccountDetailMapper accountDetailMapper;


    public void register(@Valid RegisterRequestDTO dto, MultipartFile image) {

    }
}
