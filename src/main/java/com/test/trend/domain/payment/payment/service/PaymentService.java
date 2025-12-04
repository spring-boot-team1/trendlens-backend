package com.test.trend.domain.payment.payment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.mapper.PaymentMapper;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 결제(Payment) 정보를 관리하는 서비스
 * 결제 요청, 승인, 실패 처리 등을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
	
	private final PaymentRepository repository;
	private final PaymentMapper mapper;
	
	/**
     * 결제 요청 기록
     * 
     * @param dto 결제 요청 DTO
     * @return 저장된 결제 정보 DTO
     */
	public PaymentDTO recordPaymentRequest(PaymentDTO dto) {
		Payment entity = mapper.toEntity(dto);
		Payment saved = repository.save(entity);
		return mapper.toDto(entity);
	}
	
	/**
     * 결제 승인 처리
     *
     * @param seqPayment 결제 PK
     * @return 승인된 결제 DTO
     */
	public PaymentDTO approvePayment(Long seqPayment) {
		Payment payment = repository.findById(seqPayment)
				.orElseThrow(() -> new IllegalArgumentException());
		
		Payment updated = Payment.builder()
				.seqPayment(payment.getSeqPayment())
				.seqAccount(payment.getSeqAccount())
                .seqUserSub(payment.getSeqUserSub())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus("APPROVED")
                .requestTime(payment.getRequestTime())
                .approveTime(LocalDateTime.now())
                .cancelTime(payment.getCancelTime())
                .failReason(null)
				.build();
		
		repository.save(updated);
		return mapper.toDto(updated);
	}
	
	/**
     * 결제 실패 처리
     *
     * @param seqPayment 결제 PK
     * @param failReason 실패 사유
     * @return 실패 처리된 결제 DTO
     */
	public PaymentDTO failPayment(Long seqPayment, String failReason) {
		Payment payment = repository.findById(seqPayment)
				.orElseThrow(() -> new IllegalArgumentException());
		
		Payment updated = Payment.builder()
				.seqPayment(payment.getSeqPayment())
				.seqAccount(payment.getSeqAccount())
                .seqUserSub(payment.getSeqUserSub())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus("FAILED")
                .requestTime(payment.getRequestTime())
                .approveTime(null)
                .cancelTime(null)
                .failReason(failReason)
				.build();
		
		repository.save(updated);
		return mapper.toDto(updated);
	}

}
