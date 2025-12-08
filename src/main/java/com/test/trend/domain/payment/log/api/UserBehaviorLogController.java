package com.test.trend.domain.payment.log.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.log.dto.UserBehaviorLogDTO;
import com.test.trend.domain.payment.log.service.UserBehaviorLogService;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 행동 로그(UserBehaviorLog)에 대한 REST API 컨트롤러
 * <p>
 * 사용자의 클릭, 검색, 페이지 이동 등의 이벤트를 기록하여
 * 추천 알고리즘, 통계 분석, 사용자 행동 패턴 분석에 활용됩니다.
 */
@RestController
@RequestMapping("/api/v1/user-behavior-log")
@RequiredArgsConstructor
public class UserBehaviorLogController {

	private final UserBehaviorLogService service;
	
	/**
	 * 사용자 행동 이벤트 로그를 저장합니다.
	 * <p>
	 * 프론트엔드 또는 다른 서비스에서 발생한 행동 이벤트를 전달받아
	 * 그대로 저장하고 저장된 로그 정보를 반환합니다.
	 * 
	 * @param dto 사용자 행동 이벤트 DTO
	 * @return 저장된 행동 로그 DTO
	 */
	@PostMapping
	public UserBehaviorLogDTO recordEvent(@RequestBody UserBehaviorLogDTO dto) {
		return service.recordEvent(dto);
	}
	
}
