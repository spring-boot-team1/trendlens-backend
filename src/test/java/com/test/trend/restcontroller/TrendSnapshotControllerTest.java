package com.test.trend.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.trend.api.TrendSnapshotController;
import com.test.trend.domain.payment.trend.dto.TrendSnapshotDTO;
import com.test.trend.domain.payment.trend.service.TrendSnapshotService;

@WebMvcTest(TrendSnapshotController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrendSnapshotControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TrendSnapshotService service;

	// ---------------------------------------------------------
	// 공통 Mock DTO 생성
	private TrendSnapshotDTO createMockDTO() {
		return TrendSnapshotDTO.builder()
				.seqTrendSnapshot(1L)
				.snapshotDate(LocalDateTime.of(2025, 1, 1, 10, 0))
				.topBrand("{\"brand\":\"Nike\",\"score\":92}")
				.topCategory("{\"category\":\"Shoes\",\"score\":95}")
				.styleTrend("{\"style\":\"Street\",\"score\":88}")
				.createdAt(LocalDateTime.now())
				.build();
	}

	// ---------------------------------------------------------
	// 1. 스냅샷 저장 테스트 (POST)
	@Test
	@DisplayName("POST /api/v1/trend-snapshot → 스냅샷 저장 성공")
	void testSaveSnapshot() throws Exception {

		TrendSnapshotDTO request = TrendSnapshotDTO.builder()
				.topBrand("{\"brand\":\"Nike\",\"score\":92}")
				.topCategory("{\"category\":\"Shoes\",\"score\":95}")
				.styleTrend("{\"style\":\"Street\",\"score\":88}")
				.build();

		TrendSnapshotDTO response = createMockDTO();

		when(service.saveSanpshot(any(TrendSnapshotDTO.class)))
				.thenReturn(response);

		mockMvc.perform(
				post("/api/v1/trend-snapshot")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqTrendSnapshot").value(1L))
			.andExpect(jsonPath("$.topBrand").value("{\"brand\":\"Nike\",\"score\":92}"))
			.andExpect(jsonPath("$.topCategory").value("{\"category\":\"Shoes\",\"score\":95}"))
			.andExpect(jsonPath("$.styleTrend").value("{\"style\":\"Street\",\"score\":88}"));
	}

	// ---------------------------------------------------------
	// 2. 최신 스냅샷 조회 테스트 (GET)
	@Test
	@DisplayName("GET /api/v1/trend-snapshot/latest → 최신 스냅샷 조회 성공")
	void testGetLatestSnapshot() throws Exception {

		TrendSnapshotDTO response = createMockDTO();

		when(service.getLatestSnapshot()).thenReturn(response);

		mockMvc.perform(
				get("/api/v1/trend-snapshot/latest")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqTrendSnapshot").value(1L))
			.andExpect(jsonPath("$.topBrand").exists())
			.andExpect(jsonPath("$.topCategory").exists())
			.andExpect(jsonPath("$.styleTrend").exists());
	}
}


