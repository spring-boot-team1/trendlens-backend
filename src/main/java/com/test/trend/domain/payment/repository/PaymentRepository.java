package com.test.trend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.trend.domain.payment.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
