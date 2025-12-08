package com.test.trend.restcontroller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.subscription.api.SubscriptionPlanController;
import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.service.SubscriptionPlanService;

@WebMvcTest(SubscriptionPlanController.class)
@AutoConfigureMockMvc(addFilters = false)  // 🔥 Security 필터 비활성화
class SubscriptionPlanControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SubscriptionPlanService service;

	// 공통 DTO 생성 메서드
	private SubscriptionPlanDTO createMockDTO() {
		return SubscriptionPlanDTO.builder()
				.seqSubscriptionPlan(1L)
				.planName("BASIC")
				.monthlyFee(9900)
				.durationMonth(1)
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();
	}

	// ----------------------------------------------------------------------

	@Test
	@DisplayName("POST /subscription-plans → 구독 상품 생성 성공")
	void testCreateSubscriptionPlan() throws Exception {

		SubscriptionPlanDTO request = SubscriptionPlanDTO.builder()
				.planName("PREMIUM")
				.monthlyFee(19900)
				.durationMonth(1)
				.status("ACTIVE")
				.build();

		SubscriptionPlanDTO response = SubscriptionPlanDTO.builder()
				.seqSubscriptionPlan(2L)
				.planName("PREMIUM")
				.monthlyFee(19900)
				.durationMonth(1)
				.status("ACTIVE")
				.createdAt(LocalDateTime.now())
				.build();

		when(service.create(any())).thenReturn(response);

		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/api/v1/subscription-plans")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqSubscriptionPlan").value(2L))
			.andExpect(jsonPath("$.planName").value("PREMIUM"))
			.andExpect(jsonPath("$.monthlyFee").value(19900));
	}

	// ----------------------------------------------------------------------

	@Test
	@DisplayName("GET /subscription-plans/{seqSubscriptionPlan} → 단건 조회 성공")
	void testFindById() throws Exception {

		SubscriptionPlanDTO dto = createMockDTO();

		when(service.findById(1L)).thenReturn(dto);

		mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/v1/subscription-plans/1")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqSubscriptionPlan").value(1L))
			.andExpect(jsonPath("$.planName").value("BASIC"));
	}

	// ----------------------------------------------------------------------

	@Test
	@DisplayName("GET /subscription-plans → 전체 조회 성공")
	void testFindAll() throws Exception {

		List<SubscriptionPlanDTO> list = List.of(
				createMockDTO(),
				SubscriptionPlanDTO.builder()
						.seqSubscriptionPlan(2L)
						.planName("PREMIUM")
						.monthlyFee(19900)
						.durationMonth(1)
						.status("ACTIVE")
						.createdAt(LocalDateTime.now())
						.build()
		);

		when(service.findAll()).thenReturn(list);

		mockMvc.perform(
				MockMvcRequestBuilders
						.get("/api/v1/subscription-plans")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].seqSubscriptionPlan").value(1L))
			.andExpect(jsonPath("$[1].seqSubscriptionPlan").value(2L));
	}

	// ----------------------------------------------------------------------

	@Test
	@DisplayName("PUT /subscription-plans/{id}/status → 상태 변경 성공")
	void testUpdateStatus() throws Exception {

		SubscriptionPlanDTO updated = SubscriptionPlanDTO.builder()
				.seqSubscriptionPlan(1L)
				.planName("BASIC")
				.monthlyFee(9900)
				.durationMonth(1)
				.status("INACTIVE")
				.createdAt(LocalDateTime.now())
				.build();

		when(service.updateStatus(1L, "INACTIVE")).thenReturn(updated);

		mockMvc.perform(
				MockMvcRequestBuilders
						.put("/api/v1/subscription-plans/1/status")
						.queryParam("status", "INACTIVE")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqSubscriptionPlan").value(1L))
			.andExpect(jsonPath("$.status").value("INACTIVE"));
	}

}
