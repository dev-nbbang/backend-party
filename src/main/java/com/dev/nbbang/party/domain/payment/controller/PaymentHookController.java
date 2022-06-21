package com.dev.nbbang.party.domain.payment.controller;

import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.payment.dto.request.ImportPaymentRequest;
import com.dev.nbbang.party.domain.payment.dto.request.PaymentRequest;
import com.dev.nbbang.party.domain.payment.entity.Billing;
import com.dev.nbbang.party.domain.payment.service.PaymentService;
import com.dev.nbbang.party.global.common.CommonResponse;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/payment-hook")
public class PaymentHookController {

    private final PaymentService paymentService;
    private final PartyService partyService;

    //api gateway에서 jwt token 검증을 빼야될것 같음!!!!!!!!!!!!! 수정 필요 할인시 가격을 처리하지 못했음
//    @PostMapping("/iamport-webhook")
//    public ResponseEntity<?> importWebhook(@RequestBody ImportPaymentRequest importPaymentRequest) {
//        String impUid = importPaymentRequest.getImp_uid();
//        String merchantUid = importPaymentRequest.getMerchant_uid();
//        // 0 -> memberId 1-> partyId
//        String[] merchantInfo = merchantUid.split("-");
//        int partyId = Integer.parseInt(merchantInfo[1]);
//
//        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
//        int partyPrice = partyService.findPrice((long) partyId); //메소드가 들어갈 자리
////        partyPrice = paymentService.paymentDiscount(paymentRequest, partyPrice);
//        Map<String, Object> paymentInfo = paymentService.getPaymentInfo(merchantInfo[0], merchantInfo[0], impUid);
//        if(paymentInfo == null)  return ResponseEntity.ok(CommonResponse.response(false, "결제 내역이 없습니다"));
//        if(partyPrice != -1 && paymentService.paymentCheck(paymentInfo, partyPrice)) {
//            //결제 이력 테이블에 결제 정보를 저장해줘야함함
//            paymentService.paymentLogSave(impUid, merchantInfo[0], partyId, "일반 결제 입니다.", partyPrice);
//            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
//        }
//
//        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
//    }

    @PostMapping("/iamport-callback/schedule")
    public ResponseEntity<?> importScheduleWebhook(@RequestBody ImportPaymentRequest importPaymentRequest) {
        String impUid = importPaymentRequest.getImp_uid();
        String merchantUid = importPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        String memberId = merchantInfo[0];
        Long partyId = Long.parseLong(merchantInfo[1]);

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        int partyPrice = partyService.findPrice(partyId); //메소드가 들어갈 자리
        Map<String, Object> paymentInfo = paymentService.getPaymentInfo(memberId, memberId, impUid);
        if(paymentInfo == null)  return ResponseEntity.ok(CommonResponse.response(false, "결제 내역이 없습니다"));
        if(paymentService.paymentCheck(paymentInfo, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함
            paymentService.paymentLogSave(impUid, memberId, partyId, "일반 결제 입니다.", partyPrice);
            //스케쥴 api 작성
            Billing billing = paymentService.getBilling(memberId, partyId);
            String merchantId = paymentService.schedulePayment(billing.getMerchantId(), merchantUid, partyPrice);
            Calendar cal = Calendar.getInstance();
            Date start = (Date) cal.getTime();
            cal.add(Calendar.MONTH, 1);
            billing.updateBilling(memberId, partyId, merchantId, start, (Date) cal.getTime());
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

}
