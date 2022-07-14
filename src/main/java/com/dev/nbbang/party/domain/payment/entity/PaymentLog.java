package com.dev.nbbang.party.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "PAYMENT_LOG")
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @Column(name = "PAYMENT_ID", nullable = false)
    private String paymentId;
    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;
    @Column(name = "PARTY_ID", nullable = false)
    private Long partyId;
    @Column(name = "PAYMENT_DETAIL")
    private String paymentDetail;
    @Column(name = "PAYMENT_YMD", nullable = false)
    private Timestamp paymentYmd;
    @Column(name = "PRICE", nullable = false)
    private int price;
    @Column(name = "PAYMENT_TYPE", nullable = false)
    private int paymentType;
}
