package com.test.trend.domain.crawling.content;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentDetailRepository extends JpaRepository<ContentDetail, Long>{

    List<ContentDetail> findByTargetUrl_KeywordAndAnalyzedYn(Keyword keyword, YesNo analyzedYn);

    List<ContentDetail> findByAnalyzedYn(YesNo attr0);
}
