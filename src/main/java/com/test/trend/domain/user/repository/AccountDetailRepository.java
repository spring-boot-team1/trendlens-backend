package com.test.trend.domain.user.repository;

import com.test.trend.domain.user.entity.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long> {

}
