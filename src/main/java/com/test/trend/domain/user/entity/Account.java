package com.test.trend.domain.user.entity;

import com.test.trend.domain.user.dto.AccountDTO;
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

    @Column(nullable = false, length = 50)
    private String role;

    @Column(length = 50)
    private String provider;

    @Column(length = 100)
    private String providerId;

    //builder(DTO로 변환)
    public AccountDTO toDTO(){
        return AccountDTO.builder()
                .seqAccount(this.seqAccount)
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .provider(this.provider)
                .providerId(this.providerId)
                .build();
    }
}
