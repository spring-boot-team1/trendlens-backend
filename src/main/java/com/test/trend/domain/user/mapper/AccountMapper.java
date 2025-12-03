package com.test.trend.domain.user.mapper;

import com.test.trend.domain.user.dto.AccountDTO;
import com.test.trend.domain.user.dto.RegisterRequestDTO;
import com.test.trend.domain.user.entity.Account;
import org.springframework.stereotype.Component;

/**
 * Entity/DTO 변환하는 메서드를 담은 클래스(refactoring)
 * 
 * 기존에 Entity와 DTO에 있던 builder를 유지보수를 위해 Mapper 패키지로 이동
 */
@Component
public class AccountMapper {
    //builder(엔티티로 변환)
    public Account toEntity(RegisterRequestDTO dto) {
        return Account.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) //암호화는 Service 계층에서
                .build();
    }

    //builder(DTO로 변환)
    public AccountDTO toDTO(Account account){
        return AccountDTO.builder()
                .seqAccount(account.getSeqAccount())
                .email(account.getEmail())
                .role(account.getRole())
                .provider(account.getProvider())
                .providerId(account.getProviderId())
                .build();
    }
}
