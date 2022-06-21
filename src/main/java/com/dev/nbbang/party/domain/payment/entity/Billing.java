package com.dev.nbbang.party.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "BILLING")
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BILLING_ID", nullable = false)
    private Long billingId;
    @Column(name = "CUSTOMER_ID", nullable = false)
    private String customerId;
    @Column(name = "PARTY_ID", nullable = false)
    private Long partyId;
    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;
    @Column(name = "START_YMD", nullable = false)
    private Date startYMD;
    @Column(name = "BILLING_REG_YMD", nullable = false)
    private Timestamp billingRegYMD;
    @Column(name = "END_YMD", nullable = false)
    private Date endYMD;
    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    public void updateBilling(String memberId, Long partyId, String merchantId, Date startYMD, Date endYMD) {
        this.memberId = memberId;
        this.partyId = partyId;
        this.merchantId = merchantId;
        this.startYMD = startYMD;
        this.endYMD = endYMD;
    }
}
