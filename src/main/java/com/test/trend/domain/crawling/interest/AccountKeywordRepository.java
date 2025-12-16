package com.test.trend.domain.crawling.interest;

import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.crawling.keyword.Keyword;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountKeywordRepository extends JpaRepository<AccountKeyword, Long>{

    Optional<AccountKeyword> findBySeqAccountAndKeyword(Account seqAccount, Keyword keyword);

    boolean existsBySeqAccountAndKeyword(Account seqAccount, Keyword keyword);

    List<AccountKeyword> findBySeqAccountOrderByCreatedAtDesc(Account seqAccount);

    @Query("SELECT ak.keyword FROM AccountKeyword ak WHERE ak.seqAccount.seqAccount = :seqAccount")
    List<Keyword> findKeywordsBySeqAccount(@Param("seqAccount") Long seqAccount);
}

