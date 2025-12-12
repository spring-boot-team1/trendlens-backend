package com.test.trend.enums;

public enum PaymentStatus {

	REQUESTED("결제 요청됨"),
	PENDING("결제 진행 중"),
    APPROVED("내부 승인됨"),
    DONE("Toss 승인 완료"), 
    FAILED("결제 실패"),
    CANCELED("결제 취소됨"),
    EXPIRED("결제 시도 후 만료됨"),
    PARTIAL_CANCELED("부분 취소됨");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
 // 🔥 Toss → 우리 시스템 상태 변환
    public static PaymentStatus fromTossStatus(String status) {
        if (status == null) return FAILED;

        switch (status.toUpperCase()) {
            case "READY":
            case "PENDING":
                return PENDING;

            case "DONE":
                return DONE;

            case "CANCELED":
                return CANCELED;

            case "FAILED":
                return FAILED;

            default:
                return FAILED; // Toss에서 예측 불가 상태 → 실패 처리
        }
    }
    
}
