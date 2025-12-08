package com.test.trend.domain.account.service;

import com.test.trend.domain.account.dto.CustomAccountDetails;
import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomAccountDetailsService implements UserDetailsService {
    //주입
    private final AccountRepository accountRepository;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param email the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            //로그인 성공 -> UserDetails 객체 생성
            return CustomAccountDetails.builder()
                    .email(account.getEmail())
                    .role(account.getRole().name())
                    .nickname(account.getAccountDetail().getNickname())
                    .password(account.getPassword())
                    .build();
        } else {
            //로그인 실패 -> 시큐리티 권장 사항 : 예외 발생(UsernameNotFoundException)
            throw new UsernameNotFoundException("로그인 실패: " + email);
        }
    }
}
