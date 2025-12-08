package com.test.trend.domain.crawling.freq;

import com.test.trend.domain.crawling.content.ContentDetail;
import com.test.trend.domain.crawling.content.ContentDetailRepository;
import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.keyword.KeywordRepository;
import com.test.trend.enums.YesNo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WordFrequencyService {

    private final WordFrequencyRepository wordFrequencyRepository;
    private final KeywordRepository keywordRepository;
    private final ContentDetailRepository contentDetailRepository;

    private static final Set<String> STOP_WORDS = Set.of(
            "이", "가", "은", "는", "의", "을", "를", "에", "와", "과", "도", "로", "으로",
            "있다", "없다", "합니다", "입니다", "그리고", "그래서", "하지만", "때문에",
            "정말", "너무", "진짜", "많이", "그냥", "사진", "안녕하세요", "포스팅", "오늘",
            "추천", "공유", "정보", "확인", "사용", "제품", "브랜드"
    );

    @Transactional
    public void analyzeAndSave(Long seqContentDetail, String content) {

        // 1. 본문 엔티티 조회
        ContentDetail contentDetail = contentDetailRepository.findById(seqContentDetail).orElseThrow(() -> new IllegalArgumentException("ContentDetail 없음:" + seqContentDetail));

        //2. 이미 분석된 본문이면 바로 패스
        if (contentDetail.getAnalyzedYn() == YesNo.Y) {
            return;
        }

        String bodyText = contentDetail.getBodyText();
        if (bodyText == null || bodyText.isBlank()) {
            //내용이 없더라도, 한번 건들면 다시 안하게 Y
            contentDetail.setAnalyzedYn(YesNo.Y);
            contentDetailRepository.save(contentDetail);
            return;
        }

        Keyword keyWord = contentDetail.getKeyword();

        String cleaned = bodyText
                .replace("[^a-zA-Z0-9가-힣\\s]", " ")
                .toLowerCase();

        String[] tokens = cleaned.split("\\s+");

        Map<String, Integer> wordCountMap = new HashMap<>();

        //3. 단어 카운팅
        for (String token : tokens) {
            if (token.length() <2) continue;
            if (STOP_WORDS.contains(token)) continue;

            wordCountMap.merge(token, 1, Integer::sum);
        }

        //4. WordFrequency upsert
        Long seqKeyword = keyWord.getSeqKeyword();

        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            String word = entry.getKey();
            Integer count = entry.getValue();

            WordFrequency wf = wordFrequencyRepository
                    .findByKeyword_SeqKeywordAndWord(seqKeyword, word)
                    .orElseGet(() -> {
                        WordFrequency w = new WordFrequency();
                        w.setKeyword(keyWord);
                        w.setWord(word);
                        w.setCount(0);
                        return w;
                    });

            wf.setCount(wf.getCount() + count);
            wf.setAnalyzedAt(LocalDateTime.now());

            wordFrequencyRepository.save(wf);
        }

        contentDetail.setAnalyzedYn(YesNo.Y);
        contentDetailRepository.save(contentDetail);
    }
}
