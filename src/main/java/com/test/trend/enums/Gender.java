package com.test.trend.enums;

/**
 * 성별 관련 enum
 * unknown, F , M
 */
public enum Gender {
    M,
    F,
    U;

    public static Gender fromCode(String code) {
        if (code == null) {
            return U;
        }

        String c = code.trim().toUpperCase();
        return switch (c) {
            case "M" -> M;
            case "F" -> F;
            default -> U;   // 이상한 값 들어오면 그냥 U로
        };
    }
}
