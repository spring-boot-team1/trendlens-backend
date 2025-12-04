package com.test.trend.domain.crawling.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateUtil {

    // 공통 포맷터
    public static final DateTimeFormatter BASE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter WEEK_CODE_FORMAT =
            DateTimeFormatter.ofPattern("YYYY'W'ww"); // 2025W10

    // 네이버 뉴스 pubDate 예: "Thu, 04 Dec 2025 14:22:00 +0900"
    private static final DateTimeFormatter NAVER_PUBDATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    // 2024.11.29.
    private static final DateTimeFormatter DOT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy.MM.dd.");

    // 2024-11-29
    private static final DateTimeFormatter DASH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /*-----------------------------
     * 1) DataLab API 날짜 범위 (최근 30일)
     *----------------------------*/

    /**
     * 오늘(LocalDate.now 기준) 날짜 반환
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * n일 전 날짜
     */
    public static LocalDate daysAgo(int days) {
        return LocalDate.now().minusDays(days);
    }

    /**
     * DataLab용 시작일 (기본: 30일 전)
     */
    public static String datalabStartDate(int days) {
        return daysAgo(days).format(BASE_DATE_FORMAT);
    }

    /**
     * DataLab용 종료일 (기본: 오늘)
     */
    public static String datalabEndDate() {
        return today().format(BASE_DATE_FORMAT);
    }


    /*-----------------------------
     * 2) TrendScore baseDate 처리
     *----------------------------*/

    /**
     * baseDate를 yyyy-MM-dd 문자열로 포맷
     */
    public static String formatBaseDate(LocalDate date) {
        if (date == null) return null;
        return date.format(BASE_DATE_FORMAT);
    }

    /**
     * yyyy-MM-dd 문자열을 LocalDate로 파싱
     */
    public static LocalDate parseBaseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, BASE_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("[DateUtil] parseBaseDate failed: " + dateStr);
            return null;
        }
    }


    /*-----------------------------
     * 3) WeeklyInsight weekCode 생성
     *----------------------------*/

    /**
     * weekCode 예: 2025W10
     * - 매주 배치 돌릴 때 사용
     */
    public static String weekCode(LocalDate date) {
        if (date == null) return null;
        return date.format(WEEK_CODE_FORMAT);
    }

    /**
     * 오늘 기준 주차 코드
     */
    public static String currentWeekCode() {
        return weekCode(today());
    }


    /*-----------------------------
     * 4) 배치 로그 시간 기록용
     *----------------------------*/

    /**
     * 배치 시작/종료 시각 기록용 now()
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }


    /*-----------------------------
     * 5) TargetUrl.postDate 파싱
     *----------------------------*/

    /**
     * 네이버 pubDate + 블로그/쇼핑 형태 날짜 문자열을 LocalDateTime으로 파싱
     *
     * 지원 예:
     *  - "Thu, 04 Dec 2025 14:22:00 +0900"
     *  - "2024.11.29."
     *  - "2024-11-29"
     */
    public static LocalDateTime parsePostDate(String raw) {
        if (raw == null || raw.isBlank()) return null;

        raw = raw.trim();

        // 1) 네이버 뉴스 pubDate 형태
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(raw, NAVER_PUBDATE_FORMAT);
            return zdt.toLocalDateTime();
        } catch (Exception ignored) {}

        // 2) 2024.11.29. 형태
        try {
            LocalDate d = LocalDate.parse(raw, DOT_DATE_FORMAT);
            return d.atStartOfDay();
        } catch (Exception ignored) {}

        // 3) 2024-11-29 형태
        try {
            LocalDate d = LocalDate.parse(raw, DASH_DATE_FORMAT);
            return d.atStartOfDay();
        } catch (Exception ignored) {}

        System.out.println("[DateUtil] parsePostDate failed: " + raw);
        return null;
    }
}
