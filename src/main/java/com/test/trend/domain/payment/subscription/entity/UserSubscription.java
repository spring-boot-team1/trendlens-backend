package com.test.trend.domain.payment.subscription.entity;

import java.time.LocalDateTime;

import com.test.trend.enums.SubscriptionStatus;
import com.test.trend.enums.YesNo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Long seqSubscriptionPlan;
	
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime nextBillingDate;
	
	@Enumerated(EnumType.STRING)
	private YesNo autoRenewYn;	// Y/N
	
	@Enumerated(EnumType.STRING)
	private SubscriptionStatus status;	// NOT_SUBSCRIBED/ACTIVE/CANCELED/EXPIRED
	private String cancelReason;
	
	private LocalDateTime createdAt;
	
    /** 구독 취소 */
    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELED;
        this.cancelReason = reason;
        this.autoRenewYn = YesNo.N;
    }

    /** 다음 결제일 연장 */
    public void extendBillingDate() {
        if (this.nextBillingDate == null) {
            this.nextBillingDate = LocalDateTime.now().plusMonths(1);
        } else {
            this.nextBillingDate = this.nextBillingDate.plusMonths(1);
        }
    }

}
