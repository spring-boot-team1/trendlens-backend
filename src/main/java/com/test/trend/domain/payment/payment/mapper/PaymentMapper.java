package com.test.trend.domain.payment.payment.mapper;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.entity.Payment;

public interface PaymentMapper {

    PaymentDTO toDto(Payment entity);

    Payment toEntity(PaymentDTO dto);
}
