package com.test.trend.domain.payment.trend.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.trend.dto.UserTrendHistoryDTO;
import com.test.trend.domain.payment.trend.service.UserTrendHistoryService;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 트렌드 변화 기록(UserTrendHistory)에 대한 REST API 컨트롤러
 * <p>
 * 사용자별 키워드, 트렌드 스코어, 행동 분석 결과 등을 시점별로 기록하여
 * 개인화 추천 및 트렌드 변화 분석에 활용됩니다.
 */
@RestController
@RequestMapping("/api/v1/user-trend-history")
@RequiredArgsConstructor
public class UserTrendHistoryController {

	private final UserTrendHistoryService service;
	
	/**
	 * 사용자 트렌드 기록을 저장합니다.
	 * <p>
	 * 특정 시점의 사용자 트렌드 데이터를 수집 및 저장하며,
	 * 추후 히스토리 기반 추천, 트렌드 변화 분석 등에 활용됩니다.
	 * 
	 * @param dto 사용자 트렌드 기록 DTO
	 * @return 저장된 사용자 트렌드 기록 DTO
	 */
	@PostMapping
	public UserTrendHistoryDTO recordHistory(@RequestBody UserTrendHistoryDTO dto) {
		return service.recordHistory(dto);
	}
	
}
