package com.test.trend.domain.payment.payment.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmResponse;
import com.test.trend.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqPaymentGenerator")
	@SequenceGenerator(name = "seqPaymentGenerator", sequenceName = "seqPayment", allocationSize = 1)
	private Long seqPayment;
	
	private Long seqAccount;
	private Long seqUserSub;
	
	private Long seqSubscriptionPlan;
	
	// Toss Payments 관련 필드 추가
    private String paymentKey; // Toss 고유 결제 키
    private String orderId; // 주문 고유 아이디
	
	private Long amount;
	private String paymentMethod;
	
	@Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // DONE, FAILED, CANCELED, APPROVED 등
	
	private LocalDateTime requestTime;
	private OffsetDateTime approveTime;
	private LocalDateTime cancelTime;
	
	private String failReason;
	
	 /** 결제 요청(PENDING) 레코드 생성 */
    public static Payment createPending(Long seqAccount, String orderId, Long amount) {
        return Payment.builder()
                .seqAccount(seqAccount)
                .orderId(orderId)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .requestTime(LocalDateTime.now())
                .build();
    }
	
    /**
     * Toss confirm 응답을 엔티티에 반영
     * - status, 결제키, 결제수단, 승인시간, 요청시간, 금액 등을 동기화
     */
    public void applyTossConfirm(TossPaymentConfirmResponse toss) {
    	
    	 // 🔒 중복 승인 방지
        if (this.paymentStatus == PaymentStatus.DONE) {
            return; // 이미 처리된 결제는 무시
        }
    	
        // Toss 원문 값 저장
        this.paymentKey = toss.getPaymentKey();
        this.orderId = toss.getOrderId();
        this.paymentMethod = toss.getMethod();

        // Toss 응답 기준 금액 동기화 (원하면 request amount와 비교 검증은 Service에서)
        if (toss.getTotalAmount() != null) {
            this.amount = toss.getTotalAmount();
        }

        // 상태 매핑 (네 enum에 fromTossStatus가 이미 있는 구조 기준)
        this.paymentStatus = PaymentStatus.fromTossStatus(toss.getStatus());

        // 시간 저장 (Toss DTO를 LocalDateTime으로 바꿨다는 가정)
        this.approveTime = toss.getApprovedAt();

        // DONE이 아닌데 실패 사유가 필요하면 service에서 추가로 세팅
    }

    /** 구독 생성/갱신 후 결제와 구독을 연결 */
    public void linkSubscription(Long seqUserSub) {
        this.seqUserSub = seqUserSub;
    }

    /** 내부 승인(필요 시 유지) */
    public void approve(String method, OffsetDateTime time) {
        this.paymentMethod = method;
        this.approveTime = time;
    }

    public void cancel(LocalDateTime canceledAt) {
        this.paymentStatus = PaymentStatus.CANCELED;
        this.cancelTime = canceledAt;
    }

    public void fail(String reason) {
        if (this.paymentStatus == PaymentStatus.DONE) {
            throw new IllegalStateException("이미 완료된 결제는 실패 처리할 수 없습니다.");
        }
        this.paymentStatus = PaymentStatus.FAILED;
        this.failReason = reason;
    }

    public void updateStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

	public void setSeqSubscriptionPlan(Long seqSubscriptionPlan) {
		
	}

}

