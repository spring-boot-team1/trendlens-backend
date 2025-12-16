package com.test.trend.domain.crawling.service;

import com.test.trend.domain.crawling.keyword.RisingKeywordDto;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;


@Service
public class MusinsaCategoryCrawlerService {

    @Value("${selenium.url:}")
    private String seleniumUrl;

    public List<RisingKeywordDto> crawlRisingKeywords() {
        List<RisingKeywordDto> result = new ArrayList<>();

        WebDriver driver = createDriver();

        try {
            System.out.println(">>> [Debug] 무신사 랭킹 접속...");
            driver.get("https://www.musinsa.com/main/musinsa/ranking?gf=A&storeCode=musinsa&sectionId=199&contentsId=&categoryCode=000&ageBand=AGE_BAND_ALL&subPan=product");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // 스크롤 내려서 상품 로딩 (Lazy Loading 대응)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 0; i < 3; i++) {
                js.executeScript("window.scrollBy(0, 1000)");
                Thread.sleep(2000);
            }

            // href에 'goods' 또는 'products'가 포함된 a 태그 찾기
            List<WebElement> elements = driver.findElements(By.cssSelector("a[href*='/goods/'], a[href*='/products/']"));

            System.out.println(">>> [Debug] 발견된 링크 개수: " + elements.size());

            List<ProductInfo> productInfos = new ArrayList<>();
            List<String> visitedTitles = new ArrayList<>(); // 중복 방지용

            for (WebElement el : elements) {
                if (productInfos.size() >= 20) break;

                try {
                    // [핵심 수정] 클래스 이름(.list_img 등)을 쓰지 않고 태그 구조로만 찾음

                    // 1. 이미지 URL 추출
                    String imgUrl = "";
                    try {
                        // a 태그 하위에 있는 img 태그를 찾음 (깊이 상관없이 찾음)
                        WebElement imgEl = el.findElement(By.tagName("img"));

                        // 스크린샷에 src가 있으므로 src 우선, 없으면 data-original 확인
                        imgUrl = imgEl.getAttribute("src");
                        if (!StringUtils.hasText(imgUrl)) {
                            imgUrl = imgEl.getAttribute("data-original");
                        }

                        // "//image.msscdn.net" 으로 시작하면 "https:" 붙여주기
                        if (StringUtils.hasText(imgUrl) && imgUrl.startsWith("//")) {
                            imgUrl = "https:" + imgUrl;
                        }
                    } catch (NoSuchElementException ne) {
                        // 이미지가 없는 링크(텍스트만 있는 경우)는 무시
                        continue;
                    }

                    // 2. 상품명 추출
                    String title = el.getAttribute("title"); // a태그의 title 속성 시도
                    if (!StringUtils.hasText(title)) {
                        // 없으면 이미지 태그의 alt 속성 시도 (스크린샷에 alt가 보임!)
                        try {
                            WebElement imgEl = el.findElement(By.tagName("img"));
                            title = imgEl.getAttribute("alt");
                        } catch (Exception ignored) {}
                    }
                    if (!StringUtils.hasText(title)) {
                        title = el.getText(); // 그래도 없으면 텍스트
                    }

                    if (StringUtils.hasText(title)) {
                        title = title.replace("상품 이미지", "")
                                .replaceAll("\n", " ")
                                .trim();
                    }

                    // 3. 링크 추출
                    String href = el.getAttribute("href");

                    // 필터링
                    if (href == null || href.contains("/like/") || href.contains("/cart/") || href.contains("/reviews")) continue;
                    if (!StringUtils.hasText(title) || title.length() <= 1) continue;
                    if (!StringUtils.hasText(imgUrl)) continue; // 이미지가 없으면 저장 안 함 (인사이트 화면용이라 필수)

                    title = title.replaceAll("\n", " ").trim();

                    if (!visitedTitles.contains(title)) {
                        visitedTitles.add(title);
                        productInfos.add(new ProductInfo(title, href, imgUrl));
                        System.out.println("   -> [수집] " + title); // 디버깅용
                    }

                } catch (StaleElementReferenceException e) {
                    // 스크롤 등으로 DOM이 변경되어 요소를 잃어버린 경우 건너뜀
                    System.out.println("   [Pass] 요소가 변경됨 (Stale)");
                } catch (Exception e) {
                    System.out.println("   [Error] 개별 파싱 실패: " + e.getMessage());
                }
            }

            System.out.println(">>> [Debug] 상세 페이지 진입하여 카테고리 수집 시작 (" + productInfos.size() + "개)");

            //2. 수집한 URL로 상세페이지 이동 (selenium 이동 -> Jsoup 파싱)
            for (ProductInfo info : productInfos) {
                String category = getCategoryFromDetailPage(driver, info.url);

                if ("기타".equals(category)) {
                    category = detectCategoryByKeyword(info.title);
                }

                result.add(new RisingKeywordDto(info.title, category, info.imgUrl));
                System.out.println(" + 완료: [" + category + "]" + info.title + "(Img: " + (StringUtils.hasText(info.imgUrl) ? "O" : "X") + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }

        return result;
    }

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        //공통옵션
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");

        //AWS 배포 환경
        if (StringUtils.hasText(seleniumUrl)) {
            System.out.println(">>> [System] Romote WebDriver 연결 시도:" + seleniumUrl);

            options.addArguments("--headless");              // 모니터 없는 환경
            options.addArguments("--no-sandbox");            // 리눅스 샌드박스 정책 우회
            options.addArguments("--disable-dev-shm-usage"); // 메모리 부족 방지
            options.addArguments("--disable-gpu");           // GPU 가속 끄기
            options.addArguments("--window-size=1920,1080"); // 레이아웃 깨짐 방지

            try {
                return new RemoteWebDriver(new URL(seleniumUrl), options);
            } catch (MalformedURLException e) {
                throw  new RuntimeException("Selenium URL 형식이 잘못되었습니다: " + seleniumUrl);
            }
        }
        else {
            System.out.println(">>> [System] Local ChromeDriver 실행");
            return  new ChromeDriver(options);
        }
    }

    // 상세 페이지 로직 (Selenium 이동 -> Jsoup 파싱)
    private String getCategoryFromDetailPage(WebDriver driver, String url) {
        try {

            driver.get(url);

            // 페이지 로딩 대기 (필수 요소가 뜰 때까지)
            // 무신사 상세 페이지의 브레드크럼(경로) 클래스 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            try {
                // global-breadcrumb 또는 item_categories 등 여러 클래스 중 하나라도 뜰 때까지 대기
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/category/']")));
            } catch (TimeoutException e) {
                System.out.println(" >>> [Detail] breadcrumb 대기 타임아웃, 일단 진행");
            }

            // ★ 핵심: Selenium이 렌더링한 소스를 String으로 가져옴
            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);

            // 무신사 카테고리 경로 선택자 (사이트 개편에 따라 여러 개 시도)
            Elements categoryElements = doc.select("a[data-category-name], a[href*='/category/']");

            if (categoryElements.isEmpty()) {
                System.out.println(" >>> [Detail] 경로 없음 -> 기타반환");
                return "기타";
            }
            List<String> paths = new ArrayList<>();
            for (Element cat : categoryElements) {
                // 텍스트보다 더 정확한 'data-category-name' 속성 값을 우선 추출
                String catName = cat.attr("data-category-name");

                // 속성 값이 비어있으면 텍스트(예: "스니커즈")를 가져옴
                if (catName == null || catName.isBlank()) {
                    catName = cat.text().trim();
                }

                // 필터링: "브랜드"나 "전체" 같은 건 제외하고 유효한 카테고리만
                if (!catName.isEmpty() && !catName.contains("무신사") && catName.length() < 10) {
                    paths.add(catName);
                }
            }
            // 중복 제거 (LinkedHashSet 이용)
            List<String> uniquePaths = new ArrayList<>(new LinkedHashSet<>(paths));
            if (uniquePaths.isEmpty()) return "기타";

            return mapToCoarseCategory(String.join(" > ", uniquePaths));

        } catch (Exception e) {
            System.out.println("   [Error] 상세 페이지 파싱 실패: " + e.getMessage());
            return "기타";
        }
    }

    // DTO용 내부 클래스
    private static class ProductInfo {
        String title;
        String url;
        String imgUrl;

        public ProductInfo(String title, String url, String imgUrl) {
            this.title = title;
            this.url = url;
            this.imgUrl = imgUrl;
        }
    }

    // 기존 키워드 매칭 로직 (이름 변경: detectCategory -> detectCategoryByKeyword)
    private String detectCategoryByKeyword(String keyword) {
        String lower = keyword.toLowerCase();
        if (lower.contains("맨투맨") || lower.contains("후드") || lower.contains("티셔츠") || lower.contains("셔츠") || lower.contains("니트") || lower.contains("가디건")) return "상의";
        if (lower.contains("팬츠") || lower.contains("슬랙스") || lower.contains("데님") || lower.contains("진") || lower.contains("바지") || lower.contains("스커트")) return "하의";
        if (lower.contains("패딩") || lower.contains("코트") || lower.contains("자켓") || lower.contains("점퍼") || lower.contains("플리스")) return "아우터";
        if (lower.contains("스니커즈") || lower.contains("운동화") || lower.contains("부츠") || lower.contains("로퍼") || lower.contains("샌들")) return "신발";
        if (lower.contains("가방") || lower.contains("백팩") || lower.contains("모자") || lower.contains("볼캡") || lower.contains("비니")) return "잡화";
        return "기타";
    }

    // 경로 텍스트를 대분류로 매핑하는 로직 (기존 로직 활용)
    private String mapToCoarseCategory(String categoryPath) {
        if (categoryPath == null) return "기타";

        String lower = categoryPath.toLowerCase();

        // 불필요한 코드/색상/대괄호 정리 (선택)
        lower = lower
                .replaceAll("\\[[^]]*\\]", " ")         // [블랙] 같은거 제거
                .replaceAll("[0-9]{3,}-[0-9]{2,}", " ") // FV4317-002 같은 모델코드 제거
                .replaceAll("[^가-힣a-zA-Z ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (lower.contains("뷰티") || lower.contains("향수")) {
            return "뷰티";
        }


        // ===== 상의 =====
        if (lower.contains("맨투맨") || lower.contains("후드") || lower.contains("후디")
                || lower.contains("티셔츠") || lower.contains("티셔쯔") // 오타 방어용
                || lower.contains("t셔츠") || lower.contains("t셔츠")
                || lower.contains("t-shirt") || lower.contains("tee") || lower.contains("반팔")
                || lower.contains("셔츠") || lower.contains("shirt")
                || lower.contains("니트") || lower.contains("knit")
                || lower.contains("가디건") || lower.contains("cardigan")
                || lower.contains("스웨트") || lower.contains("sweat")
                || lower.contains("크루넥") || lower.contains("crewneck")
                || lower.contains("긴팔") || lower.contains("롱슬리브")) {
            return "상의";
        }

        // ===== 하의 =====
        if (lower.contains("팬츠") || lower.contains("슬랙스") || lower.contains("슬랙")
                || lower.contains("데님") || lower.contains("denim")
                || lower.contains("청바지") || lower.contains("진") || lower.contains("jean")
                || lower.contains("조거") || lower.contains("jogger")
                || lower.contains("트레이닝") || lower.contains("조거팬츠")
                || lower.contains("바지") || lower.contains("pants") || lower.contains("trouser")
                || lower.contains("스커트") || lower.contains("skirt")
                || lower.contains("와이드팬츠") || lower.contains("shorts") || lower.contains("쇼츠")) {
            return "하의";
        }

        // ===== 아우터 =====
        if (lower.contains("패딩") || lower.contains("롱패딩") || lower.contains("숏패딩")
                || lower.contains("점퍼") || lower.contains("jumper")
                || lower.contains("자켓") || lower.contains("재킷") || lower.contains("jacket")
                || lower.contains("코트") || lower.contains("coat")
                || lower.contains("무스탕")
                || lower.contains("바람막이") || lower.contains("윈드브레이커")
                || lower.contains("다운") || lower.contains("duck down")
                || lower.contains("puffer") || lower.contains("padding")
                || lower.contains("플리스") || lower.contains("fleece")
                || lower.contains("집업") || lower.contains("zip-up")) {
            return "아우터";
        }

        // ===== 신발 =====
        if (lower.contains("신발") || lower.contains("슈즈") || lower.contains("shoes")
                || lower.contains("스니커즈") || lower.contains("sneaker")
                || lower.contains("운동화")
                || lower.contains("부츠") || lower.contains("boots")
                || lower.contains("워커") || lower.contains("boot")
                || lower.contains("로퍼") || lower.contains("loafer")
                || lower.contains("샌들") || lower.contains("슬리퍼") || lower.contains("sandals")
                || lower.contains("클로그") || lower.contains("슬라이드")) {
            return "신발";
        }

        // ===== 잡화 =====
        if (lower.contains("가방") || lower.contains("백팩") || lower.contains("backpack")
                || lower.contains("크로스백") || lower.contains("cross bag")
                || lower.contains("토트백") || lower.contains("tote")
                || lower.contains("슬링백")
                || lower.contains("모자") || lower.contains("캡") || lower.contains("cap")
                || lower.contains("볼캡") || lower.contains("버킷햇") || lower.contains("bucket")
                || lower.contains("비니") || lower.contains("beanie")
                || lower.contains("목도리") || lower.contains("머플러") || lower.contains("scarf")
                || lower.contains("장갑") || lower.contains("글러브") || lower.contains("glove")
                || lower.contains("벨트") || lower.contains("belt")
                || lower.contains("양말") || lower.contains("socks")) {
            return "잡화";
        }

        return "기타";
        }
}