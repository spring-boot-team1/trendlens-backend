package com.test.trend.domain.analyze.entity;

import com.test.trend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BODYANALYSIS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "seqBodyAnalysisGen")
    @SequenceGenerator(
            name = "seqBodyAnalysisGen",
            sequenceName = "SEQBODYANALYSIS", // CREATE SEQUENCE 필요
            allocationSize = 1
    )
    @Column(name = "SEQBODYANALYSIS")
    private Long seqBodyAnalysis;

    // FK: ACCOUNT(seqAccount) - 일단 Long으로만 들고 있다가
    // 나중에 필요하면 @ManyToOne으로 리팩토링하자
    @Column(name = "SEQACCOUNT", nullable = false)
    private Long seqAccount;

    @Column(name = "IMAGEURL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "MESHURL", length = 500)
    private String meshUrl;

    @Column(name = "HEIGHTCM", nullable = false, precision = 6, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "WEIGHTKG", nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", nullable = false, length = 5)
    private Gender gender;   // M / F / U

    @Column(name = "STATUS", length = 30)
    private String status;   // done / failed 등. 안 쓸 거면 null 방치해도 됨

    @Column(name = "CREATEDAT", updatable = false)
    private LocalDateTime createdAt;

    // DB 기본값이 'U'라서, 자바에서도 기본값 맞춰주고 싶으면 이렇게 써도 됨
    @PrePersist
    public void prePersist() {
        if (gender == null) {
            gender = Gender.U;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}