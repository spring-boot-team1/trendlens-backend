package com.test.trend.domain.user.mapper;

import com.test.trend.domain.user.dto.AccountDetailDTO;
import com.test.trend.domain.user.entity.Account;
import com.test.trend.domain.user.entity.AccountDetail;
import org.springframework.stereotype.Component;

@Component
public class AccountDetailMapper {
    //builder(DTO -> 엔티티로 변환)
    public AccountDetail toEntity(AccountDetailDTO dto, Account account) {
        return AccountDetail.builder()
                .seqAccountDetail(dto.getSeqAccountDetail())
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .phonenum(dto.getPhonenum())
                .birthday(dto.getBirthday())
                .profilepic(dto.getProfilepic())
                .account(account)
                .build();
    }

    //builder(엔티티 -> DTO로 변환)
    public AccountDetailDTO toDTO(AccountDetail entity) {
        return AccountDetailDTO.builder()
                .seqAccountDetail(entity.getSeqAccountDetail())
                .seqAccount(entity.getAccount() != null ? entity.getAccount().getSeqAccount() : null) //NullPointerException 방지
                .username(entity.getUsername())
                .nickname(entity.getNickname())
                .phonenum(entity.getPhonenum())
                .birthday(entity.getBirthday())
                .profilepic(entity.getProfilepic())
                .build();
    }
}
