package com.test.trend.domain.crawling.util;

public class HtmlCleaner {
	
	private HtmlCleaner() {}

	public static String clean(String html) {
		
		if (html == null) {
			return "";
		}
		
		String noTags = html.replaceAll("<[^>]+>", " ");
		
		String unescaped = noTags
				.replace("&quot;", "\"")
	            .replace("&apos;", "'")
	            .replace("&amp;", "&")
	            .replace("&lt;", "<")
	            .replace("&gt;", ">");
		
		return unescaped.replaceAll("\\s+", " ").trim();
	}
}
