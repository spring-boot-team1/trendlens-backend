package com.test.trend.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import com.test.trend.domain.payment.trend.api.UserTrendHistoryController;
import com.test.trend.domain.payment.trend.dto.UserTrendHistoryDTO;
import com.test.trend.domain.payment.trend.service.UserTrendHistoryService;

@WebMvcTest(UserTrendHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserTrendHistoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserTrendHistoryService service;

	// ---------------------------------------------------------
	// 공통 Mock DTO 생성
	// ---------------------------------------------------------
	private UserTrendHistoryDTO createMockDTO() {
		return UserTrendHistoryDTO.builder()
				.seqUserTrendHistory(1L)
				.seqAccount(100L)
				.seqKeyword(20L)
				.viewAt(LocalDateTime.of(2025, 1, 1, 12, 0))
				.sourcePage("HOME")
				.build();
	}

	// ---------------------------------------------------------
	// 1. 사용자 트렌드 히스토리 저장 테스트 (POST)
	// ---------------------------------------------------------
	@Test
	@DisplayName("POST /api/v1/user-trend-history → 사용자 트렌드 히스토리 저장 성공")
	void testRecordHistory() throws Exception {

		UserTrendHistoryDTO request = UserTrendHistoryDTO.builder()
				.seqAccount(100L)
				.seqKeyword(20L)
				.viewAt(LocalDateTime.of(2025, 1, 1, 12, 0))
				.sourcePage("HOME")
				.build();

		UserTrendHistoryDTO response = createMockDTO();

		when(service.recordHistory(any(UserTrendHistoryDTO.class)))
				.thenReturn(response);

		mockMvc.perform(
				post("/api/v1/user-trend-history")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqUserTrendHistory").value(1L))
			.andExpect(jsonPath("$.seqAccount").value(100L))
			.andExpect(jsonPath("$.seqKeyword").value(20L))
			.andExpect(jsonPath("$.sourcePage").value("HOME"))
			.andExpect(jsonPath("$.viewAt").exists());
	}
}
