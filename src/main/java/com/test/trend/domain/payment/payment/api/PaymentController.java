package com.test.trend.domain.payment.payment.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

/**
 * 결제(Payment) 처리에 대한 REST API 컨트롤러
 * <p>
 * 결제 요청 기록, 결제 승인 처리, 결제 실패 처리 기능을 제공한다.
 * 비즈니스 로직과 데이터 변환은 모두 Service 계층에서 담당한다.
 */
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService service;
	
	/**
	 * 결제 요청을 기록한다.
	 * <p>
	 * 사용자 요청 정보와 함께 결제 기본 정보를 저장한다.
	 * 
	 * @param dto 결제 요청 DTO
	 * @return 저장된 결제 정보 DTO
	 */
	@PostMapping
	public PaymentDTO recordPaymentRequest(@RequestBody PaymentDTO dto) {
		return service.recordPaymentRequest(dto);
	}
	
	/**
	 * 결제를 승인(Approve) 처리한다.
	 * <p>
	 * 승인 시간(approveTime)을 기록하며,
	 * paymentStatus 값을 "APPROVED" 로 변경한다.
	 * 
	 * @param seqPayment seqPayment 승인 처리할 결제 PK
	 * @return 승인된 결제 정보 DTO
	 */
	@PutMapping("/{seqPayment}/approve")
	public PaymentDTO approvePayment(@PathVariable("seqPayment") Long seqPayment) {
		return service.approvePayment(seqPayment);
	}
	
	/**
	 * 결제를 실패(Fail) 처리한다.
	 * <p>
	 * 실패 사유(failReason)를 기록하고,
	 * paymentStatus 값을 "FAILED" 로 변경한다.
	 * @param seqPayment 실패 처리할 결제 PK
	 * @param failReason 실패 사유
	 * @return 실패 처리된 결제 정보 DTO
	 */
	@PutMapping("/{seqPayment}/fail")
	public PaymentDTO failPayment(
			@PathVariable("seqPayment") Long seqPayment,
			@RequestParam("failReason") String failReason
	) {
		return service.failPayment(seqPayment, failReason);
	}
}


