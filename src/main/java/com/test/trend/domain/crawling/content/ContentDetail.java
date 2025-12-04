package com.test.trend.domain.crawling.content;

import java.time.LocalDateTime;

import com.test.trend.domain.crawling.targeturl.TargetUrl;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class ContentDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqContentDetailGenerator")
	private Long seqDetail;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seqUrl")
	private TargetUrl targetUrl;
	
	@Lob
	private String bodyText;
	private String imageUrl;
	private String status;
	private String errorMessage;
	
	private LocalDateTime craledAt;
	private String enginType;
	

}
