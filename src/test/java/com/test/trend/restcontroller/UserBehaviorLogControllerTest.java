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
import com.test.trend.domain.payment.log.api.UserBehaviorLogController;
import com.test.trend.domain.payment.log.dto.UserBehaviorLogDTO;
import com.test.trend.domain.payment.log.service.UserBehaviorLogService;

@WebMvcTest(UserBehaviorLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserBehaviorLogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserBehaviorLogService service;

	// ---------------------------------------------------------
	// 공통 Mock DTO
	// ---------------------------------------------------------
	private UserBehaviorLogDTO createMockDTO() {
		return UserBehaviorLogDTO.builder()
				.seqLog(1L)
				.seqAccount(100L)
				.eventType("CLICK")
				.eventDetail("Button A clicked")
				.eventTime(LocalDateTime.now())
				.build();
	}

	// ---------------------------------------------------------
	// 1. 사용자 행동 로그 저장 테스트 (POST)
	// ---------------------------------------------------------
	@Test
	@DisplayName("POST /api/v1/user-behavior-log → 사용자 행동 로그 저장 성공")
	void testRecordEvent() throws Exception {

		UserBehaviorLogDTO request = UserBehaviorLogDTO.builder()
				.seqAccount(100L)
				.eventType("CLICK")
				.eventDetail("Button A clicked")
				.build();

		UserBehaviorLogDTO response = createMockDTO();

		when(service.recordEvent(any(UserBehaviorLogDTO.class)))
				.thenReturn(response);

		mockMvc.perform(
				post("/api/v1/user-behavior-log")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqLog").value(1L))
			.andExpect(jsonPath("$.seqAccount").value(100L))
			.andExpect(jsonPath("$.eventType").value("CLICK"))
			.andExpect(jsonPath("$.eventDetail").value("Button A clicked"));
	}
}
