package com.test.trend.domain.crawling.targeturl;

import static com.test.trend.domain.crawling.util.DomainUtil.extractDomain;
import static com.test.trend.domain.crawling.util.DomainUtil.extractRootDomain;

import java.util.List;

import org.springframework.stereotype.Service;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.service.SearchApiService;
import com.test.trend.enums.TargetUrlStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TargetUrlService {
	
	private final TargetUrlRepository targetUrlRepository;
	private final KeywordRepository keywordRepository;
	private final SearchApiService searchApiService;
	
	   private static final List<String> NEWS_KEYWORDS = List.of(
	            "news.naver.com",
	            "n.news.naver.com",
	            "hani.co.kr",
	            "hankyung.com",
	            "mk.co.kr",
	            "chosun.com",
	            "joongang.co.kr",
	            "donga.com",
	            "khan.co.kr",
	            "segye.com",
	            "fnnews.com",
	            "inews24.com",
	            "mbn.co.kr",
	            "yonhapnews.co.kr",
	            "ytn.co.kr"
	    );

	    // 쇼핑 도메인 키워드
	    private static final List<String> SHOPPING_KEYWORDS = List.of(
	            "shopping.naver.com",
	            "smartstore.naver.com",
	            "gmarket.co.kr",
	            "11st.co.kr",
	            "coupang.com",
	            "auction.co.kr",
	            "ssg.com"
	    );

	    // 블로그 도메인 키워드
	    private static final List<String> BLOG_KEYWORDS = List.of(
	            "blog.naver.com",
	            "tistory.com",
	            "velog.io",
	            "medium.com"
	    );

	    // 매거진 도메인 키워드
	    private static final List<String> MAGAZINE_KEYWORDS = List.of(
	            "allurekorea.com",
	            "vogue.co.kr",
	            "gqkorea.co.kr",
	            "esquirekorea.co.kr",
	            "marieclairekorea.com",
	            "cosmopolitan.co.kr",
	            "elle.co.kr"
	    );
	
	@Transactional
	public int collectTargets(Long seqKeyword) {
		
		Keyword keyword = keywordRepository.findById(seqKeyword)
				.orElseThrow(() -> new IllegalArgumentException("Keyword not found"));
		
		System.out.println("[TARGET] keyword text = '" + keyword.getKeyword() + "'");
		
		List<SearchResultDto> results = 
				searchApiService.search(keyword.getKeyword());
		
		System.out.println("[TARGET] result size ="+ results.size());
		
		int saved = 0;
		
		for (SearchResultDto dto : results) {
			
			String url = dto.url();
			
			/* if (targetUrlRepository.existsByKeywordAndUrl(keyword, url)) { continue; } */
			
			
			String host = extractDomain(url);
			String root = extractRootDomain(host);
			String domainType = classifyDomain(host, root); //BLOG /NEWS/SHOPPING/MAGAZINE/ETC
			
			TargetUrl entity = TargetUrl.builder()
					.keyword(keyword)
					.url(url)
					.title(dto.title())
					.postDate(dto.postDate())
					.domain(domainType)
					.status(TargetUrlStatus.WAIT)
					.build();
			
			targetUrlRepository.save(entity);
			saved++;
		}
		
		return saved;
	}
	



	public List<TargetUrl> getWaitUrls() {
		return targetUrlRepository.findByStatus(TargetUrlStatus.WAIT);
	}

	private String classifyDomain(String host, String root) {

		if (isMatch(host, root, BLOG_KEYWORDS)) {
			return "BLOGS";
		}
		if (isMatch(host, root, SHOPPING_KEYWORDS)) {
			return "SHOPPINGS";
		}
		if (isMatch(host, root, MAGAZINE_KEYWORDS)) {
			return "MAGAZINE";
		}
		if (isMatch(host, root, NEWS_KEYWORDS)) {
			return "NEWS";
		}
		return "ETC";
	}

	private boolean isMatch(String host, String root, List<String> Keywords) {
		if (host == null) host ="";
		if (root == null) root ="";
		String h = host.toLowerCase();
		String r = root.toLowerCase();
		
		for (String key : Keywords) {
			String k = key.toLowerCase();
			if (h.contains(k) || r.contains(k)) {
				return true;
			}
		}
		return false;
	}

}
