package com.test.trend.domain.payment.trend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.trend.dto.TrendSnapshotDTO;
import com.test.trend.domain.payment.trend.service.TrendSnapshotService;

import lombok.RequiredArgsConstructor;

/**
 * 트렌드 스냅샷(TrendSnapshot)에 대한 REST API 컨트롤러
 * <p>
 * 스냅샷 저장 및 최신 스냅샷 조회 기능을 제공한다.
 * 데이터 변환 및 검증은 Service 계층에서 처리한다.
 */
@RestController
@RequestMapping("/api/v1/trend-snapshot")
@RequiredArgsConstructor
public class TrendSnapshotController {

	private final TrendSnapshotService service;
	
	/**
	 * 새로운 트렌드 스냅샷을 저장합니다.
	 * <p>
	 * 배치/크롤러가 주기적으로 생성한 스냅샷 데이터를 저장할 때 사용합니다.
	 * 
	 * @param dto 스냅샷 정보 DTO
	 * @return 저장된 스냅샷 DTO
	 */
	@PostMapping
	public TrendSnapshotDTO saveSanpshot(@RequestBody TrendSnapshotDTO dto) {
		return service.saveSanpshot(dto);
	}
	
	/**
	 * 가장 최신(가장 최근 snapshotDate)의 트렌드 스냅샷을 조회합니다.
	 * @return 최신 스냅샷 DTO (없으면 null)
	 */
	@GetMapping("/latest")
	public TrendSnapshotDTO getLatestSnapshot() {
		return service.getLatestSnapshot();
	}
	
}
