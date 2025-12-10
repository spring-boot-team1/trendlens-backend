package com.test.trend.domain.account.service;

import com.test.trend.domain.account.dto.RegisterRequestDTO;
import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.entity.AccountDetail;
import com.test.trend.domain.account.mapper.AccountDetailMapper;
import com.test.trend.domain.account.mapper.AccountMapper;
import com.test.trend.domain.account.repository.AccountDetailRepository;
import com.test.trend.domain.account.repository.AccountRepository;
import com.test.trend.domain.account.service.util.ServiceUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ResponseCache;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {
    //주입
    private final BCryptPasswordEncoder passwordEncoder; //SecurityConfig에 Bean 선언 필요
    private final AccountRepository accountRepository;
    private final AccountDetailRepository accountDetailRepository;
    private final AccountMapper accountMapper;
    private final AccountDetailMapper accountDetailMapper;
    private final ServiceUtil serviceUtil;
    private final RedisService redisService;

    @Transactional
    public void signup(RegisterRequestDTO dto) {
        //이메일 중복검사
        validateEmail(dto.getEmail());

        //비밀번호 암호화
        String encodedPW = passwordEncoder.encode(dto.getPassword());

        //생년월일 유효성검사 및 YYYY-MM-DD 형태로 파싱
        LocalDate birthday = serviceUtil.parseAndValidateBirthday(dto.getBirthday());

        //전화번호 유효성검사 및 000-0000-0000 형태로 파싱
        String phonenum = serviceUtil.parseandValidatePhoneNum(dto.getPhonenum());

        //DTO - 엔티티 변환(Mapper 사용)
        // registerRequestDTO를 Account엔티티로 변환
        Account account = accountMapper.toEntity(dto, encodedPW);

        // AccountDetailDTO를 AccountDetail 엔티티로 변환
        AccountDetail accountDetail = accountDetailMapper.toEntity(dto, account, dto.getProfilepic(),birthday, phonenum);

        // DB 저장
        accountRepository.save(account);
        accountDetailRepository.save(accountDetail);
    }

    private void validateEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    public String getProfileKey(Long seqAccount) {
        //DB 조회
        Account account = accountRepository.findBySeqAccount(seqAccount);

        if (account == null) {
            throw new RuntimeException("계정을 찾을 수 없습니다.");
        }
        String key = account.getAccountDetail().getProfilepic();
        System.out.println("AccountService >>>>> getProfileKey : key = " + key);
        if(key==null ||key.isBlank()){
            return "uploads/profilepic/8f90e5a7-3519-4a58-b8ea-a91a41e74bd8.png"; //기본 프로필 사진
        }

        return key;
    }

    public void logout(Long seqAccount, HttpServletResponse response) {
        //refreshToken 삭제
        redisService.deleteRefreshToken(seqAccount);
        // refreshtoken 쿠키삭제
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
