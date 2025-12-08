package com.test.trend.domain.crawling.content;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentDetailRepository extends JpaRepository<ContentDetail, Long>{

    List<ContentDetail> findByKeywordAndAnalyzedYn(Keyword keyword, YesNo analyzedYn);

}
