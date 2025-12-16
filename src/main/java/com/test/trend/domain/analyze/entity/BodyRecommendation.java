package com.test.trend.domain.analyze.entity;

import com.test.trend.domain.analyze.entity.BodyMetrics;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BODYRECOMMENDATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqBodyRecommendationGen")
    @SequenceGenerator(
            name = "seqBodyRecommendationGen",
            sequenceName = "seqBodyRecommendation",   // DB 시퀀스 이름
            allocationSize = 1
    )
    @Column(name = "SEQBODYRECOMMENDATION")
    private Long seqBodyRecommendation;

    // ------------------------------
    // 🔗 BodyMetrics (N:1)
    // ------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SEQBODYMETRICS", nullable = false)
    private BodyMetrics bodyMetrics;

    // ------------------------------
    // 🧠 AI 프롬프트 & 결과 (CLOB)
    // ------------------------------
    @Lob
    @Column(name = "PROMPTUSED", nullable = false)
    private String promptUsed;

    @Lob
    @Column(name = "AIRESULT", nullable = false)
    private String aiResult;

    // ------------------------------
    // 🕒 생성 시각
    // ------------------------------
    @Column(name = "CREATEDAT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
