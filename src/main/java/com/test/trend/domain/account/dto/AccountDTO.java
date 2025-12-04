package com.test.trend.domain.account.dto;

import com.test.trend.enums.Role;
import lombok.*;

/**
 * 클라이언트에게 전송하기 위한 DTO(출력용)
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long seqAccount;
    private String email;
//    private String password; //출력용이기때문에 비밀번호는 불필요함
    private Role role;
    private String provider;
    private String providerId;

}
