package com.test.trend.domain.crawling.freq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long>{

    Optional<WordFrequency> findByKeyword_SeqKeywordAndWord(Long seqKeyword, String word);

    @Query("""
            select wf.keyword.seqKeyword as seqKeyword,
                   sum(wf.count)         as totalCount
            from WordFrequency wf
            group by wf.keyword.seqKeyword
            """)
    List<WordFreqAgg> findKeywordTotalCounts();

    interface WordFreqAgg {
        Long getSeqKeyword();
        Long getTotalCount();
    }
}