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