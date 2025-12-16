package com.test.trend.domain.crawling.targeturl;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.TargetUrlStatus;

public interface TargetUrlRepository extends JpaRepository<TargetUrl, Long>{

	boolean existsByKeywordAndUrl(Keyword keyword, String url);

	List<TargetUrl> findByStatus(TargetUrlStatus wait);

    boolean existsByUrl(String url);

}
