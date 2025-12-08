package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.keyword.RisingKeywordDto;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MusinsaCategoryCrawlerService {

    public List<RisingKeywordDto> crawlRisingKeywords() {
        List<RisingKeywordDto> result = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        // options.addArguments("--headless"); // 디버깅 끝나면 주석 해제

        WebDriver driver = new ChromeDriver(options);

        try {
            System.out.println(">>> [Debug] 무신사 랭킹 접속...");
            driver.get("https://www.musinsa.com/main/musinsa/ranking?gf=A&storeCode=musinsa&sectionId=199&contentsId=&categoryCode=000&ageBand=AGE_BAND_ALL&subPan=product");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // 스크롤 내려서 상품 로딩 (Lazy Loading 대응)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 0; i < 3; i++) {
                js.executeScript("window.scrollBy(0, 1000)");
                Thread.sleep(1000);
            }

            // ★ 중요: 현재 무신사 구조에 맞는 선택자 사용
            // href에 'goods' 또는 'products'가 포함된 a 태그 찾기
            List<WebElement> elements = driver.findElements(By.cssSelector("a[href*='/goods/'], a[href*='/products/']"));

            System.out.println(">>> [Debug] 발견된 링크 개수: " + elements.size());

            List<String> visitedTitles = new ArrayList<>(); // 중복 방지용

            for (WebElement el : elements) {
                // 1. 키워드(상품명) 추출
                String title = el.getAttribute("title");
                if (title == null || title.isBlank()) {
                    title = el.getText();
                }

                // 유효성 검사 (광고, 좋아요 버튼 등 제외)
                String href = el.getAttribute("href");
                if (href == null || href.contains("/like/") || href.contains("/cart/") || href.contains("/reviews")) {
                    continue;
                }

                if (title != null && title.length() > 1) {
                    title = title.replaceAll("\n", " ").trim(); // 줄바꿈 제거

                    // 중복이 아니고, 의미 있는 단어만
                    if (!visitedTitles.contains(title)) {
                        visitedTitles.add(title);

                        // 2. 카테고리 자동 분류
                        String category = detectCategory(title);

                        // DTO에 담기
                        result.add(new RisingKeywordDto(title, category));
                        System.out.println("   + 수집: [" + category + "] " + title);
                    }
                }

                if (result.size() >= 5) break; // 20개만 수집
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return result;
    }

    // 키워드 기반 카테고리 분류 로직
    private String detectCategory(String keyword) {
        String lower = keyword.toLowerCase();

        if (lower.contains("맨투맨") || lower.contains("후드") || lower.contains("티셔츠")
                || lower.contains("셔츠") || lower.contains("니트") || lower.contains("가디건")
                || lower.contains("스웨트") || lower.contains("긴팔")) {
            return "상의";
        }

        if (lower.contains("팬츠") || lower.contains("슬랙스") || lower.contains("데님")
                || lower.contains("청바지") || lower.contains("스커트") || lower.contains("조거")
                || lower.contains("트레이닝") || lower.contains("바지")) {
            return "하의";
        }

        if (lower.contains("패딩") || lower.contains("코트") || lower.contains("자켓")
                || lower.contains("점퍼") || lower.contains("바람막이") || lower.contains("푸퍼")
                || lower.contains("플리스") || lower.contains("집업")) {
            return "아우터";
        }

        if (lower.contains("스니커즈") || lower.contains("운동화") || lower.contains("부츠")
                || lower.contains("로퍼") || lower.contains("샌들") || lower.contains("워커")) {
            return "신발";
        }

        if (lower.contains("가방") || lower.contains("백팩") || lower.contains("크로스백")
                || lower.contains("모자") || lower.contains("볼캡") || lower.contains("비니")
                || lower.contains("목도리") || lower.contains("장갑")) {
            return "잡화";
        }

        return "기타"; // 분류 안 되면 기타
    }
}