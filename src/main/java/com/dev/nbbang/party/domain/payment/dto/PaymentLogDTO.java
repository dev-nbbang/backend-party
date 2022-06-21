package com.dev.nbbang.party.domain.payment.dto;

import com.dev.nbbang.party.domain.payment.entity.PaymentLog;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PaymentLogDTO {
    private String paymentId;
    private String memberId;
    private Long partyId;
    private String paymentDetail;
    private Timestamp paymentYmd;
    private int price;
    private int paymentType;

    @Builder
    public PaymentLogDTO(String paymentId, String memberId, Long partyId, String paymentDetail, Timestamp paymentYmd, int price, int paymentType) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.partyId = partyId;
        this.paymentDetail = paymentDetail;
        this.paymentYmd = paymentYmd;
        this.price = price;
        this.paymentType = paymentType;
    }

    public static PaymentLogDTO create(PaymentLog paymentLog) {
        return PaymentLogDTO.builder()
                .paymentId(paymentLog.getPaymentId())
                .memberId(paymentLog.getMemberId())
                .partyId(paymentLog.getPartyId())
                .paymentDetail(paymentLog.getPaymentDetail())
                .paymentYmd(paymentLog.getPaymentYmd())
                .price(paymentLog.getPrice())
                .paymentType(paymentLog.getPaymentType())
                .build();
    }

    public static List<PaymentLogDTO> createList(Slice<PaymentLog> paymentLogs) {
        List<PaymentLogDTO> response = new ArrayList<>();
        for (PaymentLog paymentLog : paymentLogs) {
            response.add(PaymentLogDTO.builder()
                    .paymentId(paymentLog.getPaymentId())
                    .memberId(paymentLog.getMemberId())
                    .partyId(paymentLog.getPartyId())
                    .paymentDetail(paymentLog.getPaymentDetail())
                    .paymentYmd(paymentLog.getPaymentYmd())
                    .price(paymentLog.getPrice())
                    .paymentType(paymentLog.getPaymentType())
                    .build());
        }
        return response;
    }
}
