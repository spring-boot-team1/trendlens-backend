package com.test.trend.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.test.trend.domain.payment.subscription.api.UserSubscriptionController;
import com.test.trend.domain.payment.subscription.dto.UserSubscriptionDTO;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;
import com.test.trend.enums.SubscriptionStatus;

@WebMvcTest(UserSubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 제거 (403 방지)
class UserSubscriptionControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private UserSubscriptionService service;
	
	// 공통 Mock DTO 생성
	// ---------------------------------------------------------
	private UserSubscriptionDTO createMockDTO() {
		return UserSubscriptionDTO.builder()
				.seqUserSub(1L)
				.seqAccount(100L)
				.seqSubscriptionPlan(10L)
				.startDate(LocalDateTime.now())
				.status(SubscriptionStatus.ACTIVE)
				.build();
	}
	
	// 1. 사용자 구독 시작 테스트 (POST)
	// ---------------------------------------------------------
	@Test
	@DisplayName("POST /api/v1/user-subscription → 구독 시작 성공")
	void testStartSubscription() throws Exception {

		UserSubscriptionDTO request = UserSubscriptionDTO.builder()
				.seqAccount(100L)
				.seqSubscriptionPlan(10L)
				.build();

		UserSubscriptionDTO response = createMockDTO();

		when(service.startSubscription(any(UserSubscriptionDTO.class)))
				.thenReturn(response);

		mockMvc.perform(
				post("/api/v1/user-subscription")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqUserSub").value(1L))
			.andExpect(jsonPath("$.seqAccount").value(100L))
			.andExpect(jsonPath("$.status").value("ACTIVE"));
	}
	
	// 2. 사용자 구독 취소 테스트 (PUT)
	// ---------------------------------------------------------
	@Test
	@DisplayName("PUT /api/v1/user-subscription/{seqUserSub}/cancel → 구독 취소 성공")
	void testConcelSubscription() throws Exception {
		
		UserSubscriptionDTO canceled = UserSubscriptionDTO.builder()
				.seqUserSub(1L)
				.seqAccount(100L)
				.seqSubscriptionPlan(10L)
				.startDate(LocalDateTime.now().minusDays(5))
				.endDate(LocalDateTime.now())
				.status(SubscriptionStatus.CANCELED)
				.cancelReason("TEST_REASON")
				.build();
				
		when(service.cancelSubscription(eq(1L), eq("TEST_REASON")))
				.thenReturn(canceled);
		
		mockMvc.perform(
				put("/api/v1/user-subscription/1/cancel")
						.queryParam("cancelReason", "TEST_REASON")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqUserSub").value(1L))
			.andExpect(jsonPath("$.status").value("CANCELED"))
			.andExpect(jsonPath("$.cancelReason").value("TEST_REASON"));
		
	}
	
	
	
}


