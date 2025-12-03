package com.test.trend.domain.user.dto;

import com.test.trend.domain.user.entity.Account;
import com.test.trend.enums.Role;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long seqAccount;
    private String email;
    private String password;
    private Role role;
    private String provider;
    private String providerId;

    //builder(엔티티로 변환)
    public Account toEntity() {
        return Account.builder()
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .provider(this.provider)
                .providerId(this.providerId)
                .build();
    }
}
