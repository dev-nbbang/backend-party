package com.dev.nbbang.party.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillingPaymentRequest {
    private String customer_uid;
    private String merchant_uid;
    private int price;
}
