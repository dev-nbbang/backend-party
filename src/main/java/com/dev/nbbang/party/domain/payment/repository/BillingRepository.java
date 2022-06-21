package com.dev.nbbang.party.domain.payment.repository;

import com.dev.nbbang.party.domain.payment.entity.Billing;
import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    //스케쥴링 조회
    Billing findByMemberIdAndPartyId(String memberId, Long partyId);
    //스케쥴링 저장
    Billing save(Billing billing);
    //스케쥴링 삭제
    void deleteByMemberIdAndPartyId(String memberId, Long partyId);
}
