package com.dev.nbbang.party.domain.payment.service;


import com.dev.nbbang.party.domain.payment.dto.PaymentLogDTO;
import com.dev.nbbang.party.domain.payment.dto.request.PaymentRequest;
import com.dev.nbbang.party.domain.payment.entity.Billing;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    // 아임포트 서버에서 결제가 정상적으로 이루어졌는지 확인
    Map<String, Object> getPaymentInfo(String memberId, String merchantMemberId, String impUid);
    boolean paymentCheck(Map<String, Object> paymentInfo, int partyPrice);
    void paymentLogSave(String paymentId, String memberId, long partyId, String paymentDetail, int price);
    Map<String, Object> autoPayment(String customer_uid, String merchant_uid, int price, String memberId);
    String schedulePayment(String billingKey, String merchant_uid, int price);
    int paymentDiscount(Integer couponId, Integer couponType, Long point, int price, String memberId);
//    String getBillingKey(String memberId);
    Map<String, Object> refund(String reason, String impUid, int amount, int checksum);
    //결제 이력 조회
    List<PaymentLogDTO> getPaymentLog(String memberId, Pageable pageable);
    //정기 결제 저장
    void saveBilling(String memberId, String customerId, String merchantId, Long partyId);
    //정기 결제 조회
    Billing getBilling(String memberId, Long partyId);
    //정기 결제 삭제
    void deleteBilling(String memberId, Long partyId, String customerId, String merchantId);
}

/*
    private String customer_uid;
    private String merchant_uid;
    private int price;
    private Integer couponId;
    private Integer couponType;
    private Long point;
 */