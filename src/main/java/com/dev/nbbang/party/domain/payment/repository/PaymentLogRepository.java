package com.dev.nbbang.party.domain.payment.repository;

import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    @Query("select p from PaymentLog p where p.memberId = :memberId")
    Slice<PaymentLog> getPaymentLogList(String memberId, Pageable pageable);
}
