package com.test.trend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="AccountDetail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetail {

    @Id
    @SequenceGenerator(name = "seqAccountDetailGen", allocationSize = 1, sequenceName = "seqAccountDetail")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAccountDetailGen")
    private Long seqAccountDetail;

//    @Column(nullable = false)
//    private Long seqAccount;

    @Column(length = 50)
    private String username;

    @Column(length = 50)
    private String nickname;

    @Column(length = 50)
    private String phonenum;

    @Column
    private String birthday;

    @Column(length = 50)
    private String profilepic;

    @OneToOne
    @JoinColumn(name = "seqAccount")
    private Account account;


}
