package com.test.trend.domain.user.entity;

import com.test.trend.domain.user.dto.AccountDTO;
import com.test.trend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Account")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @SequenceGenerator(name = "seqAccountGen", allocationSize = 1, sequenceName = "seqAccount")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAccountGen")
    private Long seqAccount;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING) //enum은 기본적으로 숫자로 인식되기때문에 String이라고 알려줘야함
    @Builder.Default //이 어노테이션 없으면 default가 무시되고 null로 만들어져서 필수(초기값이 있어도 Builder를 쓰면 null로 덮어쓰기됨)
    @Column(nullable = false, length = 50)
    private Role role = Role.ROLE_USER;

    @Column(length = 50)
    private String provider;

    @Column(length = 100)
    private String providerId;

    @OneToOne(mappedBy = "account")
    private AccountDetail accountDetail;

    //builder(DTO로 변환)
    public AccountDTO toDTO(){
        return AccountDTO.builder()
                .seqAccount(this.seqAccount)
                .email(this.email)
                .role(this.role)
                .provider(this.provider)
                .providerId(this.providerId)
                .build();
    }
}
