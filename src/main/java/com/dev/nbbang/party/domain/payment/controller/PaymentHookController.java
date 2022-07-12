package com.dev.nbbang.party.domain.payment.controller;

import com.dev.nbbang.party.domain.payment.dto.request.ImportPaymentRequest;
import com.dev.nbbang.party.domain.payment.entity.Billing;
import com.dev.nbbang.party.domain.payment.service.PaymentService;
import com.dev.nbbang.party.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/payment-hook")
public class PaymentHookController {

    private final PaymentService paymentService;

    @PostMapping("/iamport-callback/schedule")
    public ResponseEntity<?> importScheduleWebhook(@RequestBody ImportPaymentRequest importPaymentRequest) {
        log.info("[PaymentHook Controller - import schedule webhook] 정기결제 스케쥴링 웹훅");
        String impUid = importPaymentRequest.getImp_uid();
        String merchantUid = importPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        String memberId = merchantInfo[0];
        Long partyId = Long.parseLong(merchantInfo[1]);

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        Billing billing = paymentService.getBilling(memberId, partyId);
        int partyPrice = (int) billing.getPrice();
        Map<String, Object> paymentInfo = paymentService.getPaymentInfo(memberId, memberId, impUid);
        if(paymentInfo == null)  return ResponseEntity.ok(CommonResponse.response(false, "결제 내역이 없습니다"));
        if(paymentService.paymentCheck(paymentInfo, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함
            paymentService.paymentLogSave(impUid, memberId, partyId, "일반 결제 입니다.", partyPrice, 0);
            //스케쥴 api 작성
            String merchantId = paymentService.schedulePayment(billing.getCustomerId(), merchantUid, partyPrice, LocalDateTime.now());
            Calendar cal = Calendar.getInstance();
            Date start = (Date) cal.getTime();
            cal.add(Calendar.MONTH, 1);
            LocalDateTime localDateTime = billing.getBillingRegYMD().toLocalDateTime();
            localDateTime.plusMonths(1);
            billing.updateBilling(billing.getCustomerId(), memberId, partyId, merchantId, start, (Date) cal.getTime(), Timestamp.valueOf(localDateTime));
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

}
