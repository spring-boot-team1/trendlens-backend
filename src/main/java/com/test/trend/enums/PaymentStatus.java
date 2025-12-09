package com.test.trend.enums;

public enum PaymentStatus {

	REQUESTED("결제 요청됨"),
    APPROVED("내부 승인됨"),
    DONE("Toss 승인 완료"), 
    FAILED("결제 실패"),
    CANCELED("결제 취소됨"),
    PARTIAL_CANCELED("부분 취소됨");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    // 🔥 문자열 → enum 변환 메서드 (매우 중요)
    public static PaymentStatus fromTossStatus(String status) {
        switch (status.toUpperCase()) {
            case "DONE":
                return DONE;
            case "CANCELED":
                return CANCELED;
            case "FAILED":
                return FAILED;
            default:
                return FAILED;  // Toss 미정의 상태 → 실패 처리
        }
    }
    
}
