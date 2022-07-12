package com.dev.nbbang.party.domain.payment.service;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.payment.dto.PaymentLogDTO;
import com.dev.nbbang.party.domain.payment.entity.Billing;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    // 아임포트 서버에서 결제가 정상적으로 이루어졌는지 확인
    Map<String, Object> getPaymentInfo(String memberId, String merchantMemberId, String impUid);
    // 결제 금액이 일치한지 확인
    boolean paymentCheck(Map<String, Object> paymentInfo, int partyPrice);
    // 결제 내역 저장
    void paymentLogSave(String paymentId, String memberId, long partyId, String paymentDetail, int price, int paymentType);
    // 정기 결제 요청
    Map<String, Object> autoPayment(String customer_uid, String merchant_uid, int price, String memberId);
    // 다음 결제 스케쥴 요청
    String schedulePayment(String billingKey, String merchant_uid, int price, LocalDateTime localDateTime);
    // Member API 할인 요청
    int paymentDiscount(Integer couponId, Integer couponType, Long point, int price, String memberId);
    // 환불 요청
    Map<String, Object> refund(String reason, String impUid, int amount, int checksum);
    //결제 이력 조회
    List<PaymentLogDTO> getPaymentLog(String memberId, Pageable pageable);
    //정기 결제 저장
    void saveBilling(String memberId, String customerId, String merchantId, Long partyId, long price);
    //정기 결제 조회
    Billing getBilling(String memberId, Long partyId);
    //정기 결제 삭제
    void deleteBilling(String memberId, Long partyId, String customerId, String merchantId);
    //일반 결제 남은 날짜 가격
    int normalDayPrice(PartyDTO partyDTO);
}