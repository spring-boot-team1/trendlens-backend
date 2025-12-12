package com.test.trend.domain.payment.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.payment.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByPaymentKey(String paymentKey);

	Optional<Payment> findByOrderId(String orderId);

}
