package com.test.trend.domain.crawling.freq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long>{

    Optional<WordFrequency> findByKeyword_SeqKeywordAndWord(Long seqKeyword, String word);

    List<WordFreqAgg> findKeywordTotalCounts();

    interface WordFreqAgg {
        Long getSeqKeyword();
        Long getTotalCount();
    }
}