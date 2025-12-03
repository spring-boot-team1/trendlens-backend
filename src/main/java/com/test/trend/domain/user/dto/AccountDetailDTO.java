package com.test.trend.domain.user.dto;

import lombok.*;

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
};