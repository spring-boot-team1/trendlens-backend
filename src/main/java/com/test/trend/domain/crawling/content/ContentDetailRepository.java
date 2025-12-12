package com.test.trend.domain.crawling.content;


import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.enums.YesNo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface ContentDetailRepository extends JpaRepository<ContentDetail, Long>{


    List<ContentDetail> findByAnalyzedYn(YesNo attr0);

    List<ContentDetail> findTop5ByTargetUrlKeywordOrderByCrawledAtDesc(Keyword keyword);

    @Query("SELECT c FROM ContentDetail c " +
            "WHERE c.bodyText LIKE %:keywordStr% " +
            "ORDER BY c.crawledAt DESC")
    List<ContentDetail> findByKeywordString(@Param("keywordStr") String keywordStr, Pageable pageable);
}

