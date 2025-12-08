package com.test.trend.domain.payment.trend.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.trend.dto.UserKeywordInsightDTO;
import com.test.trend.domain.payment.trend.service.UserKeywordInsightService;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 키워드 인사티으(UserKeywordInsight)에 대한 REST API 컨트롤러
 * <p>
 * 사용자별 키워드 점수, HOT 여부, 관심 키워드를 저장하거나
 * 특정 사용자의 HOT 키워드를 조회하는 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/user-keyword-insights")
@RequiredArgsConstructor
public class UserKeywordInsightController {

	private final UserKeywordInsightService service;
	
	/**
	 * 사용자 키워드 인사이트 정보를 저장합니다.
	 * <p>
	 * 분석된 키워드 점수(trendScore), HOT 여부(hotYn) 등 개인화 데이터 저장 시 사용됩니다.
	 * @param dto 저장할 인사이트 DTO
	 * @return 저장된 인사이트 DTO
	 */
	@PostMapping
	public UserKeywordInsightDTO saveInsight(@RequestBody UserKeywordInsightDTO dto) {
		return service.saveInsight(dto);
	}
	
	/**
	 * 특정 사용자의 HOT(Y) 키워드 목록을 조회합니다.
	 * <p>
	 * HOT(Y)로 지정된 키워드는 해당 사용자가 가장 선호하거나
	 * 최근 많이 반응한 키워드로 분석되어 추천 시스템 등에 활용합니다.
	 * 
	 * @param seqAccount 사용자 계정 PK
	 * @return HOT 키워드에 해당하는 인사이트 DTO 리스트
	 */
	@GetMapping("/{seqAccount}")
	public List<UserKeywordInsightDTO> getHotKeywords(@PathVariable("seqAccount") Long seqAccount) {
		return service.getHotKeywords(seqAccount);
	}

}


