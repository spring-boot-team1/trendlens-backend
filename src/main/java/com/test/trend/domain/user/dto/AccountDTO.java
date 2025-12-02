package com.test.trend.domain.user.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RestController;

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
    private String role;
    private String provider;
    private String providerId;

    //builder(엔티티로 전환..)
}
