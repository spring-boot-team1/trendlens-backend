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

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean UserSubscriptionService service;

    @Test
    void 구독시작_API_테스트() throws Exception {

        UserSubscriptionDTO dto = UserSubscriptionDTO.builder()
                .seqAccount(1L)
                .seqSubscriptionPlan(1L)
                .build();

        mockMvc.perform(post("/trend/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}


