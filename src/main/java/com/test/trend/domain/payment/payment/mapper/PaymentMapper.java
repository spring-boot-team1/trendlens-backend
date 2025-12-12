package com.test.trend.domain.payment.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDTO toDto(Payment entity);

    @Mapping(target = "seqPayment", ignore = true)
    @Mapping(target = "cancelTime", ignore = true)
    @Mapping(target = "failReason", ignore = true)
    Payment toEntity(PaymentDTO dto);
}
