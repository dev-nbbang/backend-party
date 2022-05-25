package com.dev.nbbang.party.domain.payment.repository;

import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
