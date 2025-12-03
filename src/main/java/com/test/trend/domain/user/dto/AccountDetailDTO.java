package com.test.trend.domain.user.dto;

import com.test.trend.domain.user.entity.Account;
import com.test.trend.domain.user.entity.AccountDetail;
import lombok.*;

/**
 * 클라이언트에게 전송할 때 사용하는 DTO(출력용..)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailDTO {

    private Long seqAccountDetail;
    private Long seqAccount;
    private String username;
    private String nickname;
    private String phonenum;
    private String birthday;
    private String profilepic;

    //builder(엔티티로 변환)
    public AccountDetail toEntity(Account account) {
        return AccountDetail.builder()
                .seqAccountDetail(this.seqAccountDetail)
                .username(this.username)
                .nickname(this.nickname)
                .phonenum(this.phonenum)
                .birthday(this.birthday)
                .profilepic(this.profilepic)
                .account(account)
                .build();
    }
}