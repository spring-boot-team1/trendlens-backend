package com.test.trend.domain.payment.payment.service;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.trend.domain.payment.payment.dto.PaymentDTO;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmRequest;
import com.test.trend.domain.payment.payment.dto.toss.TossPaymentConfirmResponse;
import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.mapper.PaymentMapper;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;
import com.test.trend.domain.payment.subscription.service.UserSubscriptionService;
import com.test.trend.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 결제(Payment) 정보를 관리하는 서비스
 * 결제 요청, 승인, 실패 처리 등을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
	
	private final PaymentRepository repository;
	private final PaymentMapper mapper;
	private final UserSubscriptionService service;
	
	@Value("${toss.secret-key}")
	private String tossSecretKey;
	
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
     * 내부 결제 승인 처리
     *
     * @param seqPayment 결제 PK
     * @return 승인된 결제 DTO
     */
	public PaymentDTO approvePayment(Long seqPayment) {
		
		Payment payment = repository.findById(seqPayment)
				.orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));
		
		Payment updated = Payment.builder()
				.seqPayment(payment.getSeqPayment())
				.seqAccount(payment.getSeqAccount())
                .seqUserSub(payment.getSeqUserSub())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(PaymentStatus.APPROVED)
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
				.orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));
		
		Payment updated = Payment.builder()
				.seqPayment(payment.getSeqPayment())
				.seqAccount(payment.getSeqAccount())
                .seqUserSub(payment.getSeqUserSub())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(PaymentStatus.FAILED)
                .requestTime(payment.getRequestTime())
                .approveTime(null)
                .cancelTime(null)
                .failReason(failReason)
				.build();
		
		repository.save(updated);
		return mapper.toDto(updated);
	}
	
	public PaymentDTO confirmTossPayment(TossPaymentConfirmRequest request) {
		
		String auth = "Basic " + Base64.getEncoder()
				.encodeToString((tossSecretKey + ":").getBytes());
		
		// 1. Toss Payment 승인요청
		TossPaymentConfirmResponse tossResponse = WebClient.create("https://api.tosspayments.com")
				.post()
				.uri("/v1/payments/confirm")
				.header("Authorization", auth)
				.header("Content-Type", "application/json")
				.bodyValue(request)
				.retrieve()
				.bodyToMono(TossPaymentConfirmResponse.class)
				.block();
		
		if (tossResponse == null) {
			throw new IllegalStateException("Toss 결제 승인 응답이 null입니다.");
		}
		
		log.info("[TOSS CONFIRM RESPONSE] {}", tossResponse);
		
		// 2. Toss 응답 → Payment 엔티티 생성
		Payment payment = Payment.builder()
				.paymentKey(tossResponse.getPaymentKey())
				.orderId(tossResponse.getOrderId())
				.paymentStatus(PaymentStatus.fromTossStatus(tossResponse.getStatus())) // DONE, CANCELED 등
                .approveTime(LocalDateTime.parse(tossResponse.getApprovedAt()))
                .paymentMethod(tossResponse.getMethod())
                .amount(request.getAmount())
				.build();
		
		Payment saved = repository.save(payment);

		// 3. 구독 갱신
		service.processPayment(saved);
		
        // 4. DB 저장 후 DTO로 변환
        return mapper.toDto(saved);
	}

}


