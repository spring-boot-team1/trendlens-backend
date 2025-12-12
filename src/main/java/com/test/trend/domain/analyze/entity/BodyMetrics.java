package com.test.trend.domain.analyze.entity;

import com.test.trend.domain.analyze.entity.BodyAnalysis;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BODYMETRICS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqBodyMetricsGen")
    @SequenceGenerator(
            name = "seqBodyMetricsGen",
            sequenceName = "seqBodyMetrics",   // 🟢 DB 시퀀스 이름 (미리 만들어둘 거라면)
            allocationSize = 1
    )
    @Column(name = "SEQBODYMETRICS")
    private Long seqBodyMetrics;

    // ------------------------------
    // 🔗 BodyAnalysis (N:1)
    // ------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SEQBODYANALYSIS", nullable = false)
    private BodyAnalysis bodyAnalysis;

    // ------------------------------
    // 📏 측정 값들 (소수점 포함 → BigDecimal)
    // ------------------------------

    // BMI (예: 18.75) → NUMBER(5,2)
    @Column(name = "BMI", precision = 5, scale = 2, nullable = false)
    private BigDecimal bmi;

    // 어깨너비 cm (예: 39.50) → NUMBER(5,2)
    @Column(name = "SHOULDERWIDTHCM", precision = 5, scale = 2, nullable = false)
    private BigDecimal shoulderWidthCm;

    // 팔 길이 cm (예: 52.30) → NUMBER(6,2)
    @Column(name = "ARMLENGTHCM", precision = 6, scale = 2, nullable = false)
    private BigDecimal armLengthCm;

    // 다리 길이 cm → NUMBER(6,2)
    @Column(name = "LEGLENGTHCM", precision = 6, scale = 2, nullable = false)
    private BigDecimal legLengthCm;

    // 몸통 길이 cm → NUMBER(6,2)
    @Column(name = "TORSOLENGTHCM", precision = 6, scale = 2, nullable = false)
    private BigDecimal torsoLengthCm;

    // ------------------------------
    // 🕒 생성 시각
    // ------------------------------
    @Column(name = "CREATEDAT", nullable = false)
    private LocalDateTime createdAt;

    // 필요하면 생성 시 자동 세팅용 헬퍼 메서드 정도는 써도 됨
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
