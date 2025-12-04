package com.test.trend.domain.account.mapper;

import com.test.trend.domain.account.dto.AccountDetailDTO;
import com.test.trend.domain.account.dto.RegisterRequestDTO;
import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.entity.AccountDetail;
import org.springframework.stereotype.Component;

@Component
public class AccountDetailMapper {

    /**
     * RegisterRequestDTO → AccountDetailDTO 변환
     * @param dto RegisterRequestDTO
     * @return AccountDetailDTO
     */
    public AccountDetailDTO fromRegisterDTO(RegisterRequestDTO dto) {
        return AccountDetailDTO.builder()
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .phonenum(dto.getPhonenum())
                .birthday(dto.getBirthday())
                .build();
    }

    /**
     * builder(DTO -> 엔티티로 변환)
     * @param dto AccountDetailDTO
     * @param account Account 엔티티
     * @return AccountDetail 엔티티
     */
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
