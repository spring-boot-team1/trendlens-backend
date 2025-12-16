package com.test.trend.domain.crawling.util;

import java.net.URI;

public class DomainUtil {
	
	public static String extractDomain(String url) {
		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			return host != null ? host : "";
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String extractRootDomain(String host) {
		if (host == null || host.isBlank()) return "";
		String[] parts = host.toLowerCase().split("\\.");
		if (parts.length <= 2) return host.toLowerCase();
		return parts[parts.length - 2] + "." + parts[parts.length -1];
	}

}
