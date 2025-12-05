package com.test.trend.domain.crawling.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrawlingTestService {
	
	private Connection connect(String url) {
		return Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/120.0.0.0 Safari/537.36")
				.timeout(5000);
	}
	
	public String testFetch(String url, String siteType) {
		try {
			Connection.Response res = connect(url).execute();
			int status = res.statusCode();
			
			Document doc = res.parse();
			String title = doc.title();
			
			Element nextData = doc.selectFirst("script#__NEXT_DATA__");
			String jsonPreview = "NO PREVIEW";
			
			if (nextData != null) {
				if ("musinsa".equalsIgnoreCase(siteType)) {
					String json = nextData.data();
					jsonPreview = json.substring(0, Math.min(json.length(), 300));
				} else if ("29cm".equalsIgnoreCase(siteType)) {
					jsonPreview = "29CM__NEXT_DATA__FOUND (length=" + nextData.data().length() + ")";
				} else {
					jsonPreview = "__NEXT_DATA__NOT FOUND";
				}
			} else {
				jsonPreview = "__NEXT_DATA__NOT FOUND";
			}
			
			return """
					status=%d
					title=%s
					
					nextDataPreview=%s
					""".formatted(status, title, jsonPreview);
		} catch (Exception e) {
			return "ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage();
		}
		
	}

}
