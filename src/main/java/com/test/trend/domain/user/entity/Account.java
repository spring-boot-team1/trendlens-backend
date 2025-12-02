package com.test.trend.domain.user.entity;

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

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = true, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = true, length = 50)
    private String provider;

    @Column(nullable = true, length = 100)
    private String providerId;
}
