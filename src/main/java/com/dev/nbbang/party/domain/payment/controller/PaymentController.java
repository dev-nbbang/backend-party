package com.dev.nbbang.party.domain.payment.controller;

import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.payment.dto.request.BillingPaymentRequest;
import com.dev.nbbang.party.domain.payment.dto.request.NormalPaymentRequest;
import com.dev.nbbang.party.domain.payment.service.PaymentService;
import com.dev.nbbang.party.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PartyService partyService;

    @PostMapping("/normal")
    public ResponseEntity<?> normalPayment(@RequestBody NormalPaymentRequest normalPaymentRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        String impUid = normalPaymentRequest.getImp_uid();
        String merchantUid = normalPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        int partyId = Integer.parseInt(merchantInfo[1]);

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        int partyPrice = partyService.findPrice((long) partyId); //메소드가 들어갈 자리

        if(paymentService.paymentCheck(memberId, merchantInfo[0], impUid, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함함
            paymentService.paymentLogSave(merchantUid, memberId, partyId, "일반 결제 입니다.", partyPrice);
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

    @PostMapping("/auto")
    public ResponseEntity<?> billingPayment(@RequestBody BillingPaymentRequest billingPaymentRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        if(paymentService.autoPayment(billingPaymentRequest, memberId)) {
            String merchantUid = billingPaymentRequest.getMerchant_uid();
            // 0 -> memberId 1-> partyId
            String[] merchantInfo = merchantUid.split("-");
            int partyId = Integer.parseInt(merchantInfo[1]);
            //결제 정보 저장
            paymentService.paymentLogSave(billingPaymentRequest.getMerchant_uid(), memberId, partyId, "정기 결제입니다", billingPaymentRequest.getPrice());
            //스케쥴 생성
            paymentService.schedulePayment(billingPaymentRequest.getCustomer_uid(), billingPaymentRequest.getMerchant_uid(), billingPaymentRequest.getPrice());
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

    //api gateway에서 jwt token 검증을 빼야될것 같음!!!!!!!!!!!!!
    @PostMapping("/iamport-webhook")
    public ResponseEntity<?> importWebhook(@RequestBody NormalPaymentRequest normalPaymentRequest) {
        String impUid = normalPaymentRequest.getImp_uid();
        String merchantUid = normalPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        int partyId = Integer.parseInt(merchantInfo[1]);

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        int partyPrice = partyService.findPrice((long) partyId); //메소드가 들어갈 자리

        if(paymentService.paymentCheck(merchantInfo[0], merchantInfo[0], impUid, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함함
            paymentService.paymentLogSave(merchantUid, merchantInfo[0], partyId, "일반 결제 입니다.", partyPrice);
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

    @PostMapping("/iamport-callback/schedule")
    public ResponseEntity<?> importScheduleWebhook(@RequestBody NormalPaymentRequest normalPaymentRequest) {
        String impUid = normalPaymentRequest.getImp_uid();
        String merchantUid = normalPaymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId
        String[] merchantInfo = merchantUid.split("-");
        int partyId = Integer.parseInt(merchantInfo[1]);

        // 회원 아이디로 빌링키 가져오는 api 작성 (근데 token값이 없음 어카지?)

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        int partyPrice = partyService.findPrice((long) partyId); //메소드가 들어갈 자리

        if(paymentService.paymentCheck(merchantInfo[0], merchantInfo[0], impUid, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함
            paymentService.paymentLogSave(merchantUid, merchantInfo[0], partyId, "일반 결제 입니다.", partyPrice);
            //스케쥴 api 작성
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }
}
