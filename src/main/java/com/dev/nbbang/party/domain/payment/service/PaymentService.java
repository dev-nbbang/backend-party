package com.dev.nbbang.party.domain.payment.service;

import com.dev.nbbang.party.domain.payment.dto.request.BillingPaymentRequest;
import com.dev.nbbang.party.domain.payment.dto.request.NormalPaymentRequest;

public interface PaymentService {
    // 아임포트 서버에서 결제가 정상적으로 이루어졌는지 확인
    boolean paymentCheck(String memberId, String merchantMemberId, String impUid, int partyPrice);
    void paymentLogSave(String paymentId, String memberId, int partyId, String paymentDetail, int price);
    boolean autoPayment(BillingPaymentRequest billingPaymentRequest, String memberId);
    void schedulePayment(String billingKey, String merchant_uid, int price);
}