package com.test.trend.domain.payment.log.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.log.dto.UserBehaviorLogDTO;
import com.test.trend.domain.payment.log.entity.UserBehaviorLog;

@Component
public class UserBehaviorLogMapper {

	public UserBehaviorLogDTO toDto(UserBehaviorLog entity) {
		return UserBehaviorLogDTO.builder()
				.seqLog(entity.getSeqLog())
				.seqAccount(entity.getSeqAccount())
	            .eventType(entity.getEventType())
	            .eventDetail(entity.getEventDetail())
	            .eventTime(entity.getEventTime())
				.build();
	}
	
	public UserBehaviorLog toEntity(UserBehaviorLogDTO dto) {
		return UserBehaviorLog.builder()
				.seqLog(dto.getSeqLog())
				.seqAccount(dto.getSeqAccount())
				.eventType(dto.getEventType())
	            .eventDetail(dto.getEventDetail())
	            .eventTime(dto.getEventTime())
				.build();
	}
	
	
	
}
