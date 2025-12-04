package com.test.trend.domain.payment.log.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserBehaviorLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqLogGenerator")
	@SequenceGenerator(name = "seqLogGenerator", sequenceName = "seqLog", allocationSize = 1)
	private Long seqLog;
	
	private Long seqAccount;
	private String eventType;
	
	@Lob
	private String eventDetail;
	private LocalDateTime eventTime;

}
