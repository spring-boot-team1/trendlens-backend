package com.test.trend.domain.payment.trend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.trend.dto.TrendSnapshotDTO;
import com.test.trend.domain.payment.trend.entity.TrendSnapshot;
import com.test.trend.domain.payment.trend.mapper.TrendSnapshotMapper;
import com.test.trend.domain.payment.trend.repository.TrendSnapshotRepository;

import lombok.RequiredArgsConstructor;

/**
 * 트렌드 스냅샷 데이터를 관리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrendSnapshotService {

	private final TrendSnapshotRepository repository;
	private final TrendSnapshotMapper mapper;
	
	/**
     * 트렌드 스냅샷을 저장합니다.
     *
     * @param dto 스냅샷 DTO
     * @return 저장된 스냅샷 DTO
     */
	public TrendSnapshotDTO saveSanpshot(TrendSnapshotDTO dto) {
		TrendSnapshot entity = mapper.toEntity(dto);
		TrendSnapshot saved = repository.save(entity);
		return mapper.toDto(saved);
	}
	
	/**
     * 가장 최신 트렌드 스냅샷을 조회합니다.
     *
     * @return 최신 스냅샷 DTO (없으면 null)
     */
	public TrendSnapshotDTO getLatestSnapshot() {
        return repository.findTopByOrderBySnapshotDateDesc()
                .map(mapper::toDto)
                .orElse(null);
	}
}
