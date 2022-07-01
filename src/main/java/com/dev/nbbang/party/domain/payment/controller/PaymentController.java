package com.dev.nbbang.party.domain.payment.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.payment.api.service.ImportAPI;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PartyService partyService;

    @PostMapping("/normal")
    public ResponseEntity<?> normalPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
//        String memberId = "null";
        String impUid = paymentRequest.getCustomer_uid();
        String merchantUid = paymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId -> 년월일
        String[] merchantInfo = merchantUid.split("-");
        long partyId = Long.parseLong(merchantInfo[1]);

        //파티 서비스에서 일일금액, 생성일, 기간을 가지고 만들어진 금액으로 비교
        //******지금 가격은 일일 가격으로 여기서 기간을 곱해서 결제 금액 산출해야됨
        PartyDTO partyDTO = partyService.findPartyByPartyId(partyId);
        LocalDateTime endDate = partyDTO.getRegYmd();
        endDate.plusDays(partyDTO.getPeriod());
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDate end = LocalDate.from(endDate);
        LocalDate now = LocalDate.from(nowDate);
        long days = ChronoUnit.DAYS.between(now, end);
        int partyPrice = (int)(partyDTO.getPrice() * days);//메소드가 들어갈 자리

        //파티 가격에서 쿠폰할인율과 포인트 사용 금액을 계산한다 (null이 아닐경우)
        //if절에서 paymentCheck && 멤버에서 쿠폰과 포인트를 사용할수 있는지 여부를 판단후 멤버에서 사용 처리 해준다.
        //이루어지지 않을시 환불처리가들어가고 실패 처리를 알려준다
        partyPrice = paymentService.paymentDiscount(paymentRequest.getCouponId(), paymentRequest.getCouponType(), paymentRequest.getPoint(), partyPrice, merchantInfo[0]);
        Map<String, Object> paymentInfo = paymentService.getPaymentInfo(memberId, merchantInfo[0], impUid);

        if(paymentInfo == null)  return ResponseEntity.ok(CommonResponse.response(false, "결제 내역이 없습니다"));
        if(partyPrice != -1 && paymentService.paymentCheck(paymentInfo, partyPrice)) {
            //결제 이력 테이블에 결제 정보를 저장해줘야함함
            paymentService.paymentLogSave(impUid, memberId, partyId, "일반 결제 입니다.", partyPrice);
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        } else {
            //환불
            int refundAmount = (int) paymentInfo.get("amount");
            Map<String, Object> refundInfo = paymentService.refund("결제 금액이 일치하지 않습니다", impUid, refundAmount, refundAmount);
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

    @PostMapping("/auto")
    public ResponseEntity<?> billingPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
//        String memberId = "null";
        String impUid = paymentRequest.getCustomer_uid();
        String merchantUid = paymentRequest.getMerchant_uid();
        // 0 -> memberId 1-> partyId -> 년월일
        String[] merchantInfo = merchantUid.split("-");
        long partyId = Long.parseLong(merchantInfo[1]);
        //ott에서 가격을 가져오고 변수에 저장
        //**********현재 ott가격만으로 비교하므로 ott가격에서 headcount만큼 나누기 해줘야함
        OttDTO ott = partyService.findOttPrice(partyId);
        int ottPrice = (int)(ott.getOttPrice()/ott.getOttHeadcount());
        //포인트 쿠폰 null 아닐시
        //포인트와 쿠폰을 사용할수 있는지 여부 판단
        //사용할수 있을시 변수에서 쿠폰 포인트 사용 한 값과 프론트에서 받은 가격을 비교해보고 일치시 결제 시작
        int ottPriceCalc = paymentService.paymentDiscount(paymentRequest.getCouponId(), paymentRequest.getCouponType(), paymentRequest.getPoint(), ottPrice, merchantInfo[0]);
        log.info(ottPriceCalc+"");

        if(ottPriceCalc >= 100 && paymentRequest.getPrice()==ottPriceCalc) {
            paymentRequest.setPrice(ottPriceCalc);
            Map<String, Object> paymentInfo = paymentService.autoPayment(paymentRequest.getCustomer_uid(), paymentRequest.getMerchant_uid(), paymentRequest.getPrice(), memberId);
            if(paymentInfo == null) return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));

            //결제 정보 저장
            paymentService.paymentLogSave(paymentInfo.get("imp_uid").toString(), memberId, partyId, "정기 결제입니다", paymentRequest.getPrice());
            //스케쥴 생성 (포인트랑 쿠폰을 사용했으면 다음 결제부터는 다시 ott가격으롤 예약)
            String merchantId = paymentService.schedulePayment(paymentRequest.getCustomer_uid(), paymentRequest.getMerchant_uid(), ottPrice);
            paymentService.saveBilling(memberId,paymentRequest.getCustomer_uid(),merchantId,partyId);
            return ResponseEntity.ok(CommonResponse.response(true, "결제 성공했습니다"));
        }

        return ResponseEntity.ok(CommonResponse.response(false, "결제 실패했습니다"));
    }

    @GetMapping(value = "/logs")
    public ResponseEntity<?> getPaymentLogs(@Param("page") int page, @Param("size") int size, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
//        String memberId = "null";
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentYmd"));
        return ResponseEntity.ok(CommonSuccessResponse.response(true, paymentService.getPaymentLog(memberId, pageRequest), "결제 이력을 조회했습니다"));
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<?> cancelSchedule(@Param("partyId") Long partyId, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
//        String memberId = "null";
        Billing billing = paymentService.getBilling(memberId, partyId);
        paymentService.deleteBilling(memberId, partyId, billing.getCustomerId(), billing.getMerchantId());
        return ResponseEntity.ok(CommonResponse.response(true, "성공적으로 해지했습니다"));
    }

}
