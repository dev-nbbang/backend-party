package com.dev.nbbang.party.domain.payment.service;

import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.payment.api.service.ImportAPI;
import com.dev.nbbang.party.domain.payment.dto.request.BillingPaymentRequest;
import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import com.dev.nbbang.party.domain.payment.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final ImportAPI importAPI;
    private final PaymentLogRepository paymentLogRepository;
    @Override
    public boolean paymentCheck(String memberId, String merchantMemberId, String impUid, int partyPrice) {

        if(memberId.equals(merchantMemberId)) {
            String accessToken = importAPI.getAccessToken();
            Map<String, Object> paymentInfo = importAPI.getPaymentInfo(accessToken, impUid);
            if(paymentInfo.get("status").equals("paid") && partyPrice == Integer.parseInt(paymentInfo.get("amount").toString())) return true;
        }
        return false;
    }

    @Override
    public void paymentLogSave(String paymentId, String memberId, int partyId, String paymentDetail, int price) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        paymentLogRepository.save(PaymentLog.builder().paymentId(paymentId).memberId(memberId).partyId(partyId).paymentDetail(paymentDetail).paymentYmd(timestamp).price(price).paymentType(0).build());
    }

    @Override
    public boolean autoPayment(BillingPaymentRequest billingPaymentRequest, String memberId) {
        String merchantUid = billingPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        if(memberId.equals(merchantInfo[0])) {
            String accessToken = importAPI.getAccessToken();
            Map<String, Object> paymentInfo = importAPI.Payment(accessToken,
                    billingPaymentRequest.getCustomer_uid(),merchantUid, billingPaymentRequest.getPrice(), "월간 이용권 정기결제");
            if(paymentInfo.get("status").equals("paid")) return true;
        }
        return false;
    }

    @Override
    public void schedulePayment(String billingKey, String merchant_uid, int price) {
        String accessToken = importAPI.getAccessToken();
        importAPI.Schedule(accessToken, billingKey, merchant_uid, price, "월간 이용권 정기결제");
    }
}
