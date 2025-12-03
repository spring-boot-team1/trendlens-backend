package com.test.trend.domain.user.entity;

import com.test.trend.domain.user.dto.AccountDetailDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

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

    //builder(DTO로 변환)
    public AccountDetailDTO toDTO() {
        return AccountDetailDTO.builder()
                .seqAccountDetail(this.seqAccountDetail)
                .seqAccount(this.account != null ? this.account.getSeqAccount() : null)
                .username(this.username)
                .nickname(this.nickname)
                .phonenum(this.phonenum)
                .birthday(this.birthday)
                .profilepic(this.profilepic)
                .build();
    }
}
