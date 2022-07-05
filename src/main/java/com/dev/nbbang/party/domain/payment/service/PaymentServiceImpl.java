package com.dev.nbbang.party.domain.payment.service;

import com.dev.nbbang.party.domain.payment.api.service.ImportAPI;
import com.dev.nbbang.party.domain.payment.api.service.MemberAPI;
import com.dev.nbbang.party.domain.payment.dto.PaymentLogDTO;
import com.dev.nbbang.party.domain.payment.dto.request.PaymentRequest;
import com.dev.nbbang.party.domain.payment.entity.Billing;
import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import com.dev.nbbang.party.domain.payment.repository.BillingRepository;
import com.dev.nbbang.party.domain.payment.repository.PaymentLogRepository;
import com.dev.nbbang.party.global.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
    private final ImportAPI importAPI;
    private final MemberAPI memberAPI;
    private final PaymentLogRepository paymentLogRepository;
    private final BillingRepository billingRepository;
    private final AesUtil aesUtil;

    @Override
    public Map<String, Object> getPaymentInfo(String memberId, String merchantMemberId, String impUid) {
        Map<String, Object> paymentInfo = null;
        if(memberId.equals(merchantMemberId)) {
            String accessToken = importAPI.getAccessToken();
            paymentInfo = importAPI.getPaymentInfo(accessToken, impUid);
        }
        return paymentInfo;
    }

    @Override
    public boolean paymentCheck(Map<String, Object> paymentInfo, int partyPrice) {
        if(paymentInfo.get("status").equals("paid") && partyPrice == Integer.parseInt(paymentInfo.get("amount").toString())) return true;
        return false;
    }

    @Override
    @Transactional
    public void paymentLogSave(String paymentId, String memberId, long partyId, String paymentDetail, int price) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        paymentLogRepository.save(PaymentLog.builder().paymentId(paymentId).memberId(memberId).partyId(partyId).paymentDetail(paymentDetail).paymentYmd(timestamp).price(price).paymentType(0).build());
    }

    @Override
    public Map<String, Object> autoPayment(String customer_uid, String merchant_uid, int price, String memberId) {
        String merchantUid = merchant_uid;
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        if(memberId.equals(merchantInfo[0])) {
            String accessToken = importAPI.getAccessToken();
            Map<String, Object> paymentInfo = importAPI.Payment(accessToken,
                    aesUtil.decrypt(customer_uid),merchantUid, price, "월간 이용권 정기결제");
            log.info("autoPaymentService" + String.valueOf(paymentInfo));
            if(paymentInfo.get("status").equals("paid")) return paymentInfo;
        }
        return null;
    }

    @Override
    public String schedulePayment(String billingKey, String merchant_uid, int price, LocalDateTime localDateTime) {
        String accessToken = importAPI.getAccessToken();
        String[] merchantInfo = merchant_uid.split("-");
        StringBuilder sb = new StringBuilder(merchantInfo[0] + "-" + merchantInfo[1] + "-" + importAPI.randomString());
        importAPI.Schedule(accessToken, aesUtil.decrypt(billingKey), sb.toString(), price, "월간 이용권 정기결제", localDateTime);
        return sb.toString();
    }

    @Override
    public int paymentDiscount(Integer couponId, Integer couponType, Long point, int price, String memberId) {
        int status = 0; // 1 쿠폰 2 포인트 3 둘다
        int calc = price;
        if(couponType!=null) {
            calc = (int)(calc * (1-(couponType/100.0)));
            log.info("coupon : " + calc);
            status+=1;
        }
        if(point!=null) {
            calc = (int)(calc-point);
            log.info("point : " + calc);
            status+=2;
        }
        if(status!=0) {
            //쿠폰이나 포인트 여부 확인후 실패시 -1반환
            if(!(memberAPI.discount(memberId, point, couponId, couponType))) {
                return -1;
            }
        }
        return calc;
    }

//    @Override
//    public String getBillingKey(String memberId) {
//        return memberAPI.getBillingKey(memberId);
//    }

    @Override
    public Map<String, Object> refund(String reason, String impUid, int amount, int checksum) {
        String accessToken = importAPI.getAccessToken();
        return importAPI.Refund(accessToken, reason, impUid, amount, checksum);
    }

    @Override
    public List<PaymentLogDTO> getPaymentLog(String memberId, Pageable pageable) {
        return PaymentLogDTO.createList(paymentLogRepository.getPaymentLogList(memberId, pageable));
    }

    @Override
    @Transactional
    public void saveBilling(String memberId, String customerId, String merchantId, Long partyId, long price) {
        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        billingRepository.save(Billing.builder()
                .customerId(aesUtil.encrypt(customerId)).merchantId(merchantId).partyId(partyId).memberId(memberId)
                .startYMD(new java.sql.Date(start.getTime())).endYMD(new java.sql.Date(cal.getTime().getTime())).billingRegYMD(Timestamp.valueOf(LocalDateTime.now())).price(price).build());
    }

    @Override
    public Billing getBilling(String memberId, Long partyId) {
        return billingRepository.findByMemberIdAndPartyId(memberId, partyId);
    }

    @Override
    @Transactional
    public void deleteBilling(String memberId, Long partyId, String customerId, String merchantId) {
        String accessToken = importAPI.getAccessToken();
        importAPI.unSchedule(accessToken,aesUtil.decrypt(customerId),merchantId);
        billingRepository.deleteByMemberIdAndPartyId(memberId, partyId);
    }


}
