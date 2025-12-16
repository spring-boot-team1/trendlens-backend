package com.test.trend.domain.payment.payment.mapper;

import org.mapstruct.Mapper;

import com.test.trend.domain.payment.payment.dto.WebhookLogDTO;
import com.test.trend.domain.payment.payment.entity.WebhookLog;

@Mapper(componentModel = "spring")
public interface WebhookLogMapper {

    WebhookLogDTO toDto(WebhookLog entity);
}
