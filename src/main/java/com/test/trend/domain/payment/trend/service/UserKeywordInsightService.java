package com.test.trend.domain.payment.trend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.trend.dto.UserKeywordInsightDTO;
import com.test.trend.domain.payment.trend.entity.UserKeywordInsight;
import com.test.trend.domain.payment.trend.mapper.UserKeywordInsightMapper;
import com.test.trend.domain.payment.trend.repository.UserKeywordInsightRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 키워드 기반 인사이트(UserKeywordInsight)를 관리하는 서비스
 *
 * 사용자별로 분석한 트렌드 점수, 관심 키워드, HOT 여부 등을 저장/조회
 * ex) 추천 알고리즘, 트렌드 예측, 마케팅 활용
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserKeywordInsightService {

	private final UserKeywordInsightRepository repository;
	private final UserKeywordInsightMapper mapper;
	
	/**
     * 사용자 키워드 인사이트 데이터 저장
     *
     * @param dto 저장할 사용자 키워드 인사이트 DTO
     * @return 저장된 인사이트 DTO
     */
	public UserKeywordInsightDTO saveInsight(UserKeywordInsightDTO dto) {
		UserKeywordInsight entity = mapper.toEntity(dto);
		UserKeywordInsight saved = repository.save(entity);
		return mapper.toDto(saved);
	}
	
	/**
     * HOT 키워드(Y)만 조회
     * 
     * @param seqAccount 사용자 계정 PK
     * @return HOT(Y) 필터링된 사용자 키워드 인사이트 리스트
     */
	public List<UserKeywordInsightDTO> getHotKeywords(Long seqAccount) {
		return repository.findBySeqAccountAndHotYn(seqAccount, "Y")
				.stream()
				.map(mapper::toDto)
				.toList();
				
	}
	
}


