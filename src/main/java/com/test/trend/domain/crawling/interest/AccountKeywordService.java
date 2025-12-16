package com.test.trend.domain.crawling.interest;

import com.test.trend.domain.account.entity.Account;
import com.test.trend.domain.account.repository.AccountRepository;
import com.test.trend.domain.crawling.insight.WeeklyInsight;
import com.test.trend.domain.crawling.insight.WeeklyInsightRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.domain.crawling.score.TrendScore;
import com.test.trend.domain.crawling.score.TrendScoreRepository;
import com.test.trend.enums.YesNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountKeywordService {

    private final AccountKeywordRepository accountKeywordRepo;
    private final KeywordRepository keywordRepo;
    private final WeeklyInsightRepository weeklyInsightRepo;
    private final AccountRepository accountRepo;
    private final TrendScoreRepository trendScoreRepo;

    public boolean toggleInterest(Long seqAccount, Long keywordId) {
        Keyword keyword = keywordRepo.findById(keywordId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 키워드입니다."));

        Account account = accountRepo.findById(seqAccount)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        //이미 등록되어 있다면 -> 삭제 (구독 취소)
        if (accountKeywordRepo.existsBySeqAccountAndKeyword(account, keyword)) {
            AccountKeyword target = accountKeywordRepo.findBySeqAccountAndKeyword(account, keyword)
                    .orElseThrow();
            accountKeywordRepo.delete(target);
            return false; //해제됨
        }

        //등록되어 있지 않다면 -> 저장 (구독)
        AccountKeyword newInterest = new AccountKeyword();

        newInterest.setSeqAccount(account);
        newInterest.setKeyword(keyword);
        newInterest.setCreatedAt(LocalDateTime.now());
        newInterest.setAlterYn(YesNo.Y);

        accountKeywordRepo.save(newInterest);
        return true;
    }

    @Transactional(readOnly = true)
    public List<TrendResponseDto> getMyInterests(Long seqAccount) {

        Account account = accountRepo.findById(seqAccount)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        //1. 내 관심 목록 가져오기
        List<AccountKeyword> interests = accountKeywordRepo.findBySeqAccountOrderByCreatedAtDesc(account);

        //2. DTO로 변환하면서 추가정보(Insight, Image) 채우기
        return interests.stream()
                .map(ak -> {
                    Keyword k = ak.getKeyword();

                    // AI 요약 정보 가져오기
                    String summary = "분석 대기 중";
                    String tip = "";
                    String weekCode = "";

                    // First 로 수정 + OrderByWeekCodeDesc 풀네임 작성
                    WeeklyInsight insight = weeklyInsightRepo.findFirstByKeywordOrderByWeekCodeDesc(k).orElse(null);
                    if (insight != null) {
                        summary = insight.getSummaryTxt();
                        tip = insight.getStylingTip();
                        weekCode = insight.getWeekCode();
                    }

                    int score = 0;
                    Double growthRate = 0.0;
                    Long prevScore = 0L;
                    String status = "stable";

                    TrendScore ts = trendScoreRepo.findFirstByKeywordOrderByBaseDateDesc(k).orElse(null);

                    if (ts != null) {
                        score = (ts.getFinalScore() != null) ? ts.getFinalScore().intValue() : 0;
                        growthRate = (ts.getGrowthRate() != null) ? ts.getGrowthRate() : 0.0;
                        prevScore = (ts.getPrevScore() != null) ? ts.getPrevScore() : 0L;
                        status = (ts.getStatus() != null) ? ts.getStatus() : "stable";
                    }
                    return TrendResponseDto.builder()
                            .seqKeyword(k.getSeqKeyword())
                            .keyword(k.getKeyword())
                            .category(k.getCategory())
                            .trendDate(weekCode.isEmpty() ? LocalDate.now().toString() : weekCode)
                            .aiSummary(summary)
                            .aiStylingTip(tip)
                            .imgUrl(k.getImgUrl())

                            .trendScore(score)
                            .growthRate(growthRate)
                            .prevScore(prevScore)
                            .status(status)

                            .build();
                })
                .collect(Collectors.toList());
    }

}
