package com.test.trend.domain.crawling.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class JsoupCrawlerService {

    public record CrawledResult(String content, String imageUrl) {}
    
    // 패션 관련 문맥 단어(이 단어들 중에 2개 이상 포함되어야 합격)
    private static final List<String> FASHION_CONTEXT_WORDS = Arrays.asList(
            "코디", "착샷", "사이즈", "데일리룩", "스타일", "핏", "원단", "무드", "OOTD", "착장",
            "추천", "리뷰", "후기", "구매", "착용", "디자인", "컬러", "질감", "기장", "마감","배송",
            "옷", "바지", "상의", "하의", "아우터", "신발", "가방", "모자", "악세사리", "매치", 
            "데이터", "겨울", "여름", "봄", "가을", "출근", "캠퍼스", "여행"
    );
    
    private static final List<String> SPAM_WORDS = Arrays.asList(
            "대출", "수익", "원고료", "협찬", "소정의", "문의", "상담", "가입",
            "맛집", "시술"
    );

    public CrawledResult verifyAndGetContent(String url) {
        try {
            //1. Jsoup 접속
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            // 2. 네이버 블로그 iframe 처리
            Element iframe = doc.select("iframe#mainFrame").first();
            if (iframe != null) {
                String realUrl = "https://blog.naver.com" + iframe.attr("src");
                doc = Jsoup.connect(realUrl)
                        .userAgent("Mozilla/5.0 ...")
                        .timeout(5000)
                        .get();
            }

            // 3. 본문 추출 (네이버 블로그 본문 영역)
            String content = doc.select(".se-main-container").text();

           // 본문이 없으면 구형 에디터 영역 시도
            if (content.isEmpty()) {
                content = doc.select("#postViewArea").text();
            }

            // 4. 이미지 추출
            String imageUrl = "";
            Element imgElement = doc.select(".se-main-container img").first();
            if (imgElement != null) {
                imageUrl = imgElement.attr("src");
                if (imageUrl.isEmpty()) {
                    imageUrl = imgElement.attr("data-src");
                }
            }

            // [검증단계] 진짜 유효한 글인지 확인
            if (isRelevantContent(content)) {
                return new CrawledResult(content, imageUrl);
            } else {
                System.out.println(" --> [검증 탈락] 관련성 낮음: " + url);
                return  null;
            }

        } catch (IOException e) {
            return null;
        }
    }

    private boolean isRelevantContent(String content) {
        if (content == null || content.length() < 100) {
            return false;   //너무 짧은 글 탈락 (사진만 있는 글)
        }

        for (String spam : SPAM_WORDS) {
            if (content.contains(spam)) {
                System.out.println(" --> [필터링] 스팸 의심 단어 발견:" + spam);
                return false;
            }
        }

        int contextScore = 0;
        for (String word : FASHION_CONTEXT_WORDS) {
            if (content.contains(word)) {
                contextScore++;
            }
            if (contextScore >= 2) return true;
        }

        return false;
    }

}