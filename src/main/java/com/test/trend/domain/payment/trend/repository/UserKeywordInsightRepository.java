package com.test.trend.domain.payment.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.trend.entity.UserKeywordInsight;

public interface UserKeywordInsightRepository extends JpaRepository<UserKeywordInsight, Long> {

	List<UserKeywordInsight> findBySeqAccountAndHotYn(Long seqAccount, String string);

}
