package com.test.trend.domain.payment.trend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.trend.dto.UserTrendHistoryDTO;
import com.test.trend.domain.payment.trend.entity.UserTrendHistory;
import com.test.trend.domain.payment.trend.mapper.UserTrendHistoryMapper;
import com.test.trend.domain.payment.trend.repository.UserTrendHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTrendHistoryService {

	private final UserTrendHistoryRepository repository;
	private final UserTrendHistoryMapper mapper;
	
	public UserTrendHistoryDTO recordHistory(UserTrendHistoryDTO dto) {
		UserTrendHistory entity = mapper.toEntity(dto);
		UserTrendHistory saved = repository.save(entity);
		return mapper.toDto(saved);
	}
	
}
