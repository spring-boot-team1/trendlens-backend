package com.test.trend.domain.payment.payment.mapper;

import org.springframework.stereotype.Component;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.entity.Payment;

@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDTO toDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        return PaymentDTO.builder()
                .seqPayment(entity.getSeqPayment())
                .seqAccount(entity.getSeqAccount())
                .orderId(entity.getOrderId())
                .paymentKey(entity.getPaymentKey())
                .amount(entity.getAmount())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .requestTime(entity.getRequestTime())
                .approveTime(entity.getApproveTime())
                .cancelTime(entity.getCancelTime())
                .build();
    }

	@Override
	public Payment toEntity(PaymentDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}
}

