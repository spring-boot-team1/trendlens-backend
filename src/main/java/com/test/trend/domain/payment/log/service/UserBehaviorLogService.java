package com.test.trend.domain.payment.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.log.dto.UserBehaviorLogDTO;
import com.test.trend.domain.payment.log.entity.UserBehaviorLog;
import com.test.trend.domain.payment.log.mapper.UserBehaviorLogMapper;
import com.test.trend.domain.payment.log.repository.UserBehaviorLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 행동 로그(UserBehaviorLog)를 처리하는 서비스
 * 
 * 사용자 클릭, 페이지 이동, 검색 행동 등 다양한 이벤트를 저장하여
 * 추후 사용자 행동 분석, 추천 시스템 고도화, 대시보드 통계에 활용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserBehaviorLogService {

	private final UserBehaviorLogRepository repository;
	private final UserBehaviorLogMapper mapper;
	
	/**
	 * 사용자 행동 로그 저장
	 * @param dto 저장할 사용자 행동 로그 DTO
	 * @return 저장된 행동 로그 DTO
	 */
	public UserBehaviorLogDTO recordEvent(UserBehaviorLogDTO dto) {
		UserBehaviorLog entity = mapper.toEntity(dto);
		UserBehaviorLog saved = repository.save(entity);
		return mapper.toDto(saved);
	}
	
}
