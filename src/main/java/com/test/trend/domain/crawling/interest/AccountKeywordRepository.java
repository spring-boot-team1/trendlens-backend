package com.test.trend.domain.crawling.interest;

import com.test.trend.domain.crawling.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountKeywordRepository extends JpaRepository<AccountKeyword, Long>{

    Optional<AccountKeyword> findBySeqAccountAndKeyword(Long seqAccount, Keyword keyword);

    boolean existsBySeqAccountAndKeyword(Long seqAccount, Keyword keyword);

    List<AccountKeyword> findBySeqAccountOrderByCreatedAtDesc(Long seqAccount);
}
