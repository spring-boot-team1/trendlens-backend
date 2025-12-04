package com.test.trend.domain.user.dto;

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

}