package com.test.trend.domain.payment.payment.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.entity.Payment;

@Component
public class PaymentMapper {

	public PaymentDTO toDto(Payment entity) {
		return PaymentDTO.builder()
				.seqPayment(entity.getSeqPayment())
				.seqAccount(entity.getSeqAccount())
	            .seqUserSub(entity.getSeqUserSub())
	            .amount(entity.getAmount())
	            .paymentMethod(entity.getPaymentMethod())
	            .paymentStatus(entity.getPaymentStatus())
	            .requestTime(entity.getRequestTime())
	            .approveTime(entity.getApproveTime())
	            .cancelTime(entity.getCancelTime())
	            .failReason(entity.getFailReason())
				.build();
	}
	
	public Payment toEntity(PaymentDTO dto) {
		return Payment.builder()
				.seqPayment(dto.getSeqPayment())
				.seqAccount(dto.getSeqAccount())
	            .seqUserSub(dto.getSeqUserSub())
	            .amount(dto.getAmount())
	            .paymentMethod(dto.getPaymentMethod())
	            .paymentStatus(dto.getPaymentStatus())
	            .requestTime(dto.getRequestTime())
	            .approveTime(dto.getApproveTime())
	            .cancelTime(dto.getCancelTime())
	            .failReason(dto.getFailReason())
				.build();
	}
	
}
