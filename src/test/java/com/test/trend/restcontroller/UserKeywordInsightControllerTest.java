package com.test.trend.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.trend.api.UserKeywordInsightController;
import com.test.trend.domain.payment.trend.dto.UserKeywordInsightDTO;
import com.test.trend.domain.payment.trend.service.UserKeywordInsightService;

@WebMvcTest(UserKeywordInsightController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserKeywordInsightControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserKeywordInsightService service;

	// ---------------------------------------------------------
	// 공통 Mock DTO 생성
	private UserKeywordInsightDTO createMockDTO(Long seqInsight, Long seqKeyword, String hotYn) {
		return UserKeywordInsightDTO.builder()
				.seqUserKeywordInsight(seqInsight)
				.seqAccount(100L)
				.seqKeyword(seqKeyword)
				.insightText("{\"trend\":\"AI\",\"score\":95}")
				.trendScore(95.0)
				.hotYn(hotYn)
				.createdAt(LocalDateTime.now())
				.build();
	}

	// ---------------------------------------------------------
	// 1. 인사이트 저장 테스트 (POST)
	@Test
	@DisplayName("POST /api/v1/user-keyword-insights → 인사이트 저장 성공")
	void testSaveInsight() throws Exception {

		UserKeywordInsightDTO request = UserKeywordInsightDTO.builder()
				.seqAccount(100L)
				.seqKeyword(10L)
				.insightText("{\"trend\":\"AI\",\"score\":95}")
				.trendScore(95.0)
				.hotYn("Y")
				.build();

		UserKeywordInsightDTO response = createMockDTO(1L, 10L, "Y");

		when(service.saveInsight(any(UserKeywordInsightDTO.class)))
				.thenReturn(response);

		mockMvc.perform(
				post("/api/v1/user-keyword-insights")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqUserKeywordInsight").value(1L))
			.andExpect(jsonPath("$.seqKeyword").value(10L))
			.andExpect(jsonPath("$.hotYn").value("Y"))
			.andExpect(jsonPath("$.insightText").value("{\"trend\":\"AI\",\"score\":95}"));
	}

	// ---------------------------------------------------------
	// 2. 특정 계정의 HOT 키워드 조회 테스트 (GET)
	@Test
	@DisplayName("GET /api/v1/user-keyword-insights/{seqAccount} → HOT 키워드 조회 성공")
	void testGetHotKeywords() throws Exception {

		List<UserKeywordInsightDTO> list = List.of(
				createMockDTO(1L, 10L, "Y"),
				createMockDTO(2L, 20L, "Y")
		);

		when(service.getHotKeywords(eq(100L))).thenReturn(list);

		mockMvc.perform(
				get("/api/v1/user-keyword-insights/100")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].seqKeyword").value(10L))
			.andExpect(jsonPath("$[1].seqKeyword").value(20L))
			.andExpect(jsonPath("$[0].hotYn").value("Y"));
	}
}
