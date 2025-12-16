package com.test.trend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.test.trend.domain.payment.payment.entity.Payment;
import com.test.trend.domain.payment.payment.repository.PaymentRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository repository;

    @Test
    void 결제정보_저장_조회() {

        Payment pay = Payment.builder()
                .seqAccount(1L)
                .amount(10000L)
                .paymentMethod("CARD")
                .build();

        Payment saved = repository.save(pay);

        Payment found = repository.findById(saved.getSeqPayment()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualTo(10000);
    }
}

