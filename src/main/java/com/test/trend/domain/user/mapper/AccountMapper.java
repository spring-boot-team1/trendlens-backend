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
    /**
     * builder(RegisterRequestDTO -> Account)
     * DTO -> 엔티티로 변환
     * @param dto RegisterRequestDTO
     * @return Entity
     */
    public Account toEntity(RegisterRequestDTO dto) {
        return Account.builder()
                .email(dto.getEmail())
                .password(null) //암호화는 Service 계층에서 진행, 서비스 계층에서 setPassword 필요
                .build();
    }

    /**
     * Account → AccountDTO (응답용)
     * @param entity
     * @return AccountDTO
     */
    public AccountDTO toDTO(Account entity){
        return AccountDTO.builder()
                .seqAccount(entity.getSeqAccount())
                .email(entity.getEmail())
                .role(entity.getRole())
                .provider(entity.getProvider())
                .providerId(entity.getProviderId())
                .build();
    }
}
