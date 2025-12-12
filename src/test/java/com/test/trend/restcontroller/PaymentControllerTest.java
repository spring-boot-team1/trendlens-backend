package com.test.trend.restcontroller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.payment.api.PaymentController;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.service.PaymentService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean PaymentService service;

    @Test
    void Toss결제승인_API_테스트() throws Exception {

        TossPaymentConfirmRequest req = new TossPaymentConfirmRequest();
        req.setPaymentKey("pay123");
        req.setOrderId("order_123");
        req.setAmount(10000L);

        mockMvc.perform(post("/trend/api/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}

