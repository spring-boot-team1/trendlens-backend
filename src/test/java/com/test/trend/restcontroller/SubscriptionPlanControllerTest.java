package com.test.trend.restcontroller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.trend.domain.payment.subscription.api.SubscriptionPlanController;
import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.service.SubscriptionPlanService;

@WebMvcTest(SubscriptionPlanController.class)
class SubscriptionPlanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SubscriptionPlanService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 구독상품_등록_API_테스트() throws Exception {

        SubscriptionPlanDTO dto = SubscriptionPlanDTO.builder()
                .planName("TEST")
                .planDescription("테스트")
                .monthlyFee(10000L)
                .durationMonth(1)
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/trend/api/v1/subscription/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());
    }
}
