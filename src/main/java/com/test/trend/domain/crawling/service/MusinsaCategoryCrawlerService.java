package com.test.trend.domain.crawling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.targeturl.TargetUrl;
import com.test.trend.domain.crawling.targeturl.TargetUrlRepository;
import com.test.trend.enums.TargetUrlStatus;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MusinsaCategoryCrawlerService {

    private final TargetUrlRepository targetUrlRepository;
    private final KeywordRepository keywordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Connection connect(String url) {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/120.0.0.0 Safari/537.36")
                .timeout(5000);
    }

    /**
     * 무신사 카테고리 페이지 1개를 크롤링해서
     * 상품 상세 URL을 TargetUrl(WAIT)로 저장
     *
     * @param categoryUrl  무신사 카테고리 URL (맨투맨 카테고리 등)
     * @param seqKeyword   이 URL들을 묶어줄 Keyword PK
     * @return 새로 저장된 URL 개수
     */
    public int crawlCategory(String categoryUrl, Long seqKeyword) throws Exception {

        // ★ Keyword 엔티티 조회
        Keyword keyword = keywordRepository.findById(seqKeyword)
                .orElseThrow(() -> new IllegalArgumentException("Keyword not found: " + seqKeyword));

        Document doc = connect(categoryUrl).get();

        Element nextDataScript = doc.selectFirst("script#__NEXT_DATA__");
        if (nextDataScript == null) {
            throw new IllegalStateException("__NEXT_DATA__ script not found");
        }

        String json = nextDataScript.data();
        // "__NEXT_DATA__" 안에서 /app/goods/ 로 시작하는 URL 문자열만 추출

        Pattern pattern = Pattern.compile("\"(\\/app\\/goods\\/\\d+[^\"}]*)\"");
        Matcher matcher = pattern.matcher(json);

        int inserted = 0;

        while (matcher.find()) {
            String detailUrl = matcher.group(1); // "/app/goods/123456?..."
            if (detailUrl.startsWith("/")) {
                detailUrl = "https://www.musinsa.com" + detailUrl;
            }

            if (targetUrlRepository.existsByUrl(detailUrl)) {
                continue;
            }

            TargetUrl entity = TargetUrl.builder()
                    .keyword(keyword)
                    .url(detailUrl)
                    .title(null)                 // 상품명은 나중에 상세페이지 크롤링 때 채워도 됨
                    .postDate(LocalDateTime.now())
                    .domain("MUSINSA")
                    .build();                    // status/createdAt은 @PrePersist로 자동 셋팅

            targetUrlRepository.save(entity);
            inserted++;
        }

        System.out.println(">>> Inserted Musinsa URLs: " + inserted);
        return inserted;
    }

    /**
     * JSON 트리 전체를 돌면서 goods 정보가 들어있는 노드를 찾아낸다.
     * (goodsNo + goodsName 기준)
     */
    private void collectGoods(JsonNode node, List<MusinsaProduct> out) {

        if (node.isObject()) {

            if (node.has("goodsNo") && node.has("goodsName")) {

                System.out.println(">>> goods node hit:" + node.toString().substring(0, Math.min(200, node.toString().length())));

                String goodsNo = node.get("goodsNo").asText();
                String name = node.get("goodsName").asText();

                // 상세 페이지 링크 후보 필드 (실제 JSON 보고 필요하면 수정)
                String link = null;
                if (node.has("linkUrl")) {
                    link = node.get("linkUrl").asText();
                } else if (node.has("goodsUrl")) {
                    link = node.get("goodsUrl").asText();
                } else if (node.has("url")) {
                    link = node.get("url").asText();
                }

                // 썸네일 이미지 후보 필드 (지금은 안 쓰지만 일단 보관)
                String imageUrl = null;
                if (node.has("imageUrl")) {
                    imageUrl = node.get("imageUrl").asText();
                } else if (node.has("image")) {
                    imageUrl = node.get("image").asText();
                } else if (node.has("img")) {
                    imageUrl = node.get("img").asText();
                }

                out.add(new MusinsaProduct(goodsNo, name, link, imageUrl));
            }

            node.fields().forEachRemaining(entry -> collectGoods(entry.getValue(), out));

        } else if (node.isArray()) {
            for (JsonNode child : node) {
                collectGoods(child, out);
            }
        }
    }

    /**
     * 내부용 DTO (엔티티 아님)
     */
    private record MusinsaProduct(
            String goodsNo,
            String name,
            String link,
            String imageUrl
    ) {}
}
