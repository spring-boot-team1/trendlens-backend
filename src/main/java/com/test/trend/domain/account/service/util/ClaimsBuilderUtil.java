package com.test.trend.domain.account.service.util;

import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClaimsBuilderUtil {
     private final AccountRepository repository;
     public Map<String, Object> buildAccessClaims(Long seqAccount) {
         Account account = repository.findBySeqAccount(seqAccount);

         Map<String, Object> claims = new HashMap<>();

         claims.put("seqAccount", seqAccount);
         claims.put("email", account.getEmail());
         claims.put("role", account.getRole());
         claims.put("provider", account.getProvider());
         claims.put("providerId", account.getProviderId());
         claims.put("seqAccountDetail", account.getAccountDetail().getSeqAccountDetail());
         claims.put("nickname", account.getAccountDetail().getNickname());
         claims.put("username", account.getAccountDetail().getUsername());
         claims.put("profilepic", account.getAccountDetail().getProfilepic());
         System.out.println("AuthService >>>>> AccessToken에 담길 Claims: " + claims);

         return claims;
     }
}
