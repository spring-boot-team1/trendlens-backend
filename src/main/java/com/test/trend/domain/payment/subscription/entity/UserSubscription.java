package com.test.trend.domain.payment.subscription.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.test.trend.enums.SubscriptionStatus;
import com.test.trend.enums.YesNo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSubscription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUserSubGenerator")
	@SequenceGenerator(name = "seqUserSubGenerator", sequenceName = "seqUserSub", allocationSize = 1)
	private Long seqUserSub;
	
	// FK
	private Long seqAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seqSubscriptionPlan")
	private SubscriptionPlan seqSubscriptionPlan;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime nextBillingDate;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private YesNo autoRenewYn = YesNo.N;;	// Y/N
	
	@Enumerated(EnumType.STRING)
	private SubscriptionStatus status;	// ACTIVE/CANCELED/EXPIRED
	
	private String cancelReason;
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	/** 구독 생성 팩토리 메서드 */
    public static UserSubscription create(Long seqAccount, SubscriptionPlan plan) {
        LocalDateTime now = LocalDateTime.now();

        return UserSubscription.builder()
                .seqAccount(seqAccount)
                .seqSubscriptionPlan(plan)
                .startDate(now)
                .endDate(now.plusMonths(plan.getDurationMonth()))
                .nextBillingDate(now.plusMonths(plan.getDurationMonth()))
                .autoRenewYn(YesNo.Y)
                .status(SubscriptionStatus.ACTIVE)
                .createdAt(now)
                .build();
    }
	
	 /** 정기 결제 성공 시 기간 연장 */
    public void extendBillingDate(int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("months must be positive");
        }

        LocalDateTime base = (nextBillingDate != null)
                ? nextBillingDate
                : (startDate != null ? startDate : LocalDateTime.now());

        this.nextBillingDate = base.plusMonths(months);
    }
	
    /** 구독 취소(즉시 해지 아님, autoRenewYn만 off) */
    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELED;
        this.cancelReason = reason;
        this.autoRenewYn = YesNo.N;
    }

    /** 구독 만료 처리 */
    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
    }

	public void updateStatus(SubscriptionStatus canceled) {
		
	}
}
