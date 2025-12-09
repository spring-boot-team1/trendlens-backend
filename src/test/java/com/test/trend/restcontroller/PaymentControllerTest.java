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
import com.test.trend.domain.payment.payment.api.PaymentController;
import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.service.PaymentService;
import com.test.trend.enums.PaymentStatus;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private PaymentService service;
	
	// 공통 Mock DTO
	// ---------------------------------------------------------
	private PaymentDTO createMockDTO() {
		return PaymentDTO.builder()
				.seqPayment(1L)
				.seqAccount(100L)
				.amount(9900L)
				.paymentStatus(PaymentStatus.REQUESTED)
				.requestTime(LocalDateTime.now())
				.build();
	}
	
	// 1. 결제 요청 기록 테스트 (POST)
	// ---------------------------------------------------------
	@Test
	@DisplayName("POST /api/v1/payment → 결제 요청 기록 성공")
	void testRecordPaymentRequest() throws Exception {
		
		PaymentDTO request = PaymentDTO.builder()
				.seqAccount(100L)
				.amount(9900L)
				.build();
		
		PaymentDTO response = createMockDTO();
		
		when(service.recordPaymentRequest(any(PaymentDTO.class)))
				.thenReturn(response);
		
		mockMvc.perform(
				post("/api/v1/payment")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.seqPayment").value(1L))
			.andExpect(jsonPath("$.paymentStatus").value("REQUESTED"));
	}
	
	// 2. 결제 승인 테스트 (PUT)
	// ---------------------------------------------------------
	@Test
	@DisplayName("PUT /api/v1/payment/{seqPayment}/approve → 결제 승인 성공")
	void testApprovePayment() throws Exception {

		PaymentDTO approved = PaymentDTO.builder()
				.seqPayment(1L)
				.seqAccount(100L)
				.amount(9900L)
				.paymentStatus(PaymentStatus.APPROVED)
				.approveTime(LocalDateTime.now())
				.build();

		when(service.approvePayment(eq(1L)))
				.thenReturn(approved);

		mockMvc.perform(
				put("/api/v1/payment/1/approve")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentStatus").value("APPROVED"));
	}

	// 3. 결제 실패 테스트 (PUT)
	// ---------------------------------------------------------
	@Test
	@DisplayName("PUT /api/v1/payment/{seqPayment}/fail → 결제 실패 처리 성공")
	void testFailPayment() throws Exception {

		PaymentDTO failed = PaymentDTO.builder()
				.seqPayment(1L)
				.seqAccount(100L)
				.amount(9900L)
				.paymentStatus(PaymentStatus.FAILED)
				.failReason("CARD_ERROR")
				.cancelTime(LocalDateTime.now())
				.build();

		when(service.failPayment(eq(1L), eq("CARD_ERROR")))
				.thenReturn(failed);

		mockMvc.perform(
				put("/api/v1/payment/1/fail")
						.queryParam("failReason", "CARD_ERROR")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paymentStatus").value("FAILED"))
			.andExpect(jsonPath("$.failReason").value("CARD_ERROR"));
	}

}
