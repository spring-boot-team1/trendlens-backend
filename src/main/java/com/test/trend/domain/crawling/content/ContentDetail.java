package com.test.trend.domain.crawling.content;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.keyword.Keyword;
import com.test.trend.domain.crawling.targeturl.TargetUrl;

import com.test.trend.enums.YesNo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ContentDetail")
@SequenceGenerator(
        name = "seqContentDetailGenerator",
        sequenceName = "seqContentDetail",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqContentDetailGenerator")
    private Long seqDetail;

    // 관계 매핑이 되어 있지만, 단순 저장을 위해 String url도 임시로 허용하거나
    // 실제로는 TargetUrl 객체를 set 해야 합니다. (일단 파이프라인 작동을 위해 필드 추가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seqUrl")
    private TargetUrl targetUrl;

    @Lob
    private String bodyText; // 본문 내용
    private String imageUrl;
    private String status;
    private String errorMessage;
    private LocalDateTime crawledAt = LocalDateTime.now();
    private String engineType;
    private YesNo analyzedYn = YesNo.N;

}