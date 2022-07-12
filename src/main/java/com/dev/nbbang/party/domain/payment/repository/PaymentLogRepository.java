package com.dev.nbbang.party.domain.payment.repository;

import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    // 결제 로그 페이징 처리후 리스트 반환
    @Query("select p from PaymentLog p where p.memberId = :memberId")
    Slice<PaymentLog> getPaymentLogList(String memberId, Pageable pageable);
    //해당 결제 내역중 가장 최근것 반환
    PaymentLog findTopByMemberIdAndPartyIdAndPaymentTypeOrderByPaymentYmdDesc(String memberId, long partyId, int paymentType);
    // 회원 탈퇴 시 결제 이력 전체 삭제
    void deleteAllByMemberId(String memberId);
}
