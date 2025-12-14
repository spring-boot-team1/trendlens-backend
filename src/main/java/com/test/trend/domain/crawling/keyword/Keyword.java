package com.test.trend.domain.crawling.keyword;

import java.time.LocalDateTime;

import com.test.trend.enums.YesNo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Keyword")
@SequenceGenerator(
		name = "seqKeywordGenerator",
		sequenceName = "seqKeyword",
		allocationSize = 1
		)
@Getter
@Setter
@NoArgsConstructor
public class Keyword {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqKeywordGenerator")
	private Long seqKeyword;
	private String keyword;
	private String category;
    private String imgUrl;
	
	@Enumerated(EnumType.STRING)
	private YesNo isActive = YesNo.Y;
	
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;

    @PrePersist
    public void onCreate() {
    	LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updateAt == null) {
            updateAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updateAt = LocalDateTime.now();
    }
	

}
