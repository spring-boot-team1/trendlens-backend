package com.test.trend.domain.payment.subscription.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.trend.domain.payment.subscription.dto.SubscriptionPlanDTO;
import com.test.trend.domain.payment.subscription.entity.SubscriptionPlan;
import com.test.trend.domain.payment.subscription.mapper.SubscriptionPlanMapper;
import com.test.trend.domain.payment.subscription.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

/**
 * 구독 상품(SubscriptionPlan) 도메인의 비즈니스 로직을 처리하는 서비스.
 * <p>
 * - 구독 상품 생성<br>
 * - 단건 조회 및 전체 조회<br>
 * - 상태값(status) 변경<br>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;
    private final SubscriptionPlanMapper mapper;

    /**
     * 새로운 구독 상품을 생성한다.
     *
     * @param dto 생성할 구독 상품 정보
     * @return 생성된 구독 상품 정보
     */
    public SubscriptionPlanDTO create(SubscriptionPlanDTO dto) {
        SubscriptionPlan entity = mapper.toEntity(dto);
        SubscriptionPlan saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    /**
     * PK로 구독 상품을 조회한다.
     *
     * @param seqSubscriptionPlan 구독 상품 PK
     * @return 조회된 구독 상품 정보 (없으면 null)
     */
    @Transactional(readOnly = true)
    public SubscriptionPlanDTO findById(Long seqSubscriptionPlan) {
        return repository.findById(seqSubscriptionPlan)
                .map(mapper::toDto)
                .orElse(null);
    }

    /**
     * 모든 구독 상품 목록을 조회한다.
     *
     * @return 구독 상품 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * 구독 상품의 상태(status)를 변경한다.
     *
     * @param seqSubscriptionPlan 구독 상품 PK
     * @param newStatus           변경할 상태값 (예: ACTIVE / INACTIVE)
     * @return 상태가 변경된 구독 상품 정보
     */
    public SubscriptionPlanDTO updateStatus(Long seqSubscriptionPlan, String newStatus) {
        SubscriptionPlan entity = repository.findById(seqSubscriptionPlan)
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionPlan not found"));

        entity.updateStatus(newStatus);
        return mapper.toDto(entity);
    }
}
