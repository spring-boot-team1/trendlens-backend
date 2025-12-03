package com.test.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.entity.Payment;
import com.test.trend.domain.payment.repository.PaymentRepository;

@SpringBootTest
@Transactional
public class PaymentRepositoryTest {

	@Autowired
	private PaymentRepository repository;
	
	@Test
	void createPayment() {
		Payment payment = Payment.builder()
				.seqAccount(1L)
				.seqUserSub(5L)
				.amount(9900)
				.paymentMethod("CARD")
				.paymentStatus("REQUESTED")
				.requestTime(LocalDateTime.now())
				.build();
		
		Payment saved = repository.save(payment);
		
		assertThat(saved.getSeqPayment()).isNotNull();
		assertThat(saved.getPaymentStatus()).isEqualTo("REQUESTED");
	}
	
	@Test
    void updatePaymentStatus() {
        Payment payment = repository.save(
                Payment.builder()
                        .seqAccount(1L)
                        .seqUserSub(5L)
                        .amount(9900)
                        .paymentMethod("CARD")
                        .paymentStatus("REQUESTED")
                        .requestTime(LocalDateTime.now())
                        .build()
        );

        payment.approve();
        Payment updated = repository.save(payment);

        assertThat(updated.getPaymentStatus()).isEqualTo("APPROVED");
        assertThat(updated.getApproveTime()).isNotNull();
    }
	
}
