package com.test.trend.domain.crawling.keyword;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long>{

    // 정확한 일치
    Optional<Keyword> findByKeyword(String keyword);

    // 점수(TrendScore)가 높은 순으로 검색
    @Query("SELECT ts.keyword FROM TrendScore ts " +
            "WHERE ts.keyword.keyword LIKE %:keyword% " +
            "ORDER BY ts.finalScore DESC")
    List<Keyword> findBestMatchByScore(@Param("keyword") String keyword, Pageable pageable);

    // 점수 상관엇이 이름이 포함된 것 아무거나 하나 검색
    
    // 기존 랭킹 조회용 (혹시 쓰신다면 유지)
    @Query("SELECT k FROM Keyword k JOIN TrendScore ts ON k.seqKeyword = ts.keyword.seqKeyword ORDER BY ts.finalScore DESC")
    List<Keyword> findTopTrends(Pageable pageable);

    Keyword findFirstByKeywordContaining(String searchKeyword);
}

