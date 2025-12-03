package com.test.trend.domain.user.repository;

import com.test.trend.domain.user.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

//Account 테이블의 PK(seqAccount) 자료형: Long
public interface AccountRepository extends JpaRepository<Account, Long> {

}
