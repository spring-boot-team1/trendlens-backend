package com.test.trend.domain.crawling.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsoupCrawlerService {

    public record CrawledResult(String content, String imageUrl) {}
    private final String[] FASHION_TERMS = {"코디", "착샷", "사이즈", "데일리룩", "스타일", "핏", "원단", "무드", "OOTD" };

    public CrawledResult verifyAndGetContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            // iframe 처리
            Element iframe = doc.select("iframe#mainFrame").first();
            if (iframe != null) {
                String realUrl = "https://blog.naver.com" + iframe.attr("src");
                doc = Jsoup.connect(realUrl)
                        .userAgent("Mozilla/5.0 ...")
                        .timeout(5000)
                        .get();
            }

            // 1. 본문 추출
            String content = doc.select(".se-main-container").text();

            // 2. 이미지 추출 (본문 내 첫 번째 이미지)
            String imageUrl = "";
            Element imgElement = doc.select(".se-main-container img").first(); // 본문 내 첫 이미지
            if (imgElement != null) {
                imageUrl = imgElement.attr("src");

                // 가끔 썸네일이 data-src에 있는 경우 대비
                if (imageUrl.isEmpty()) {
                    imageUrl = imgElement.attr("data-src");
                }
            }

            if (content.isEmpty()) return null;

            // 3. 검증
            if (isFashionContent(content)) {
                // 텍스트와 이미지를 함께 반환
                return new CrawledResult(content, imageUrl);
            } else {
                return null;
            }

        } catch (IOException e) {
            return null;
        }
    }

    private boolean isFashionContent(String content) {
        int fashionScore = 0;
        int noiseScore = 0;

        for (String term : FASHION_TERMS) {
            if (content.contains(term)) fashionScore++;
        }

        if (fashionScore == 0) return false;
        if (noiseScore > fashionScore * 2) return false;

        return true;
    }
}