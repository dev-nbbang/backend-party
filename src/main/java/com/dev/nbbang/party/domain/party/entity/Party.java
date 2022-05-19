package com.dev.nbbang.party.domain.party.entity;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARTY")
@Getter
@NoArgsConstructor
@DynamicInsert
public class Party {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "PARTY_ID")
    private Long partyId;

    @OneToOne
    @JoinColumn(name = "OTT_ID")
    private Ott ott;     // OTT로 바뀌어야함

    @Column(name = "LEADER_ID")
    private String leaderId;

    @Column(name = "PRESENT_HEADCOUNT")
    private Integer presentHeadcount;

    @Column(name = "MAX_HEADCOUNT")
    private Integer maxHeadcount;

    @Column(name = "REG_YMD")
    private LocalDateTime regYmd;

    @Column(name = "OTT_ACC_ID")
    private String ottAccId;

    @Column(name = "OTT_ACC_PW")
    private String ottAccPw;

    @Column(name = "MATCHING_TYPE")
    private Integer matchingType;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "PARTY_DETAIL")
    private String partyDetail;

    @Column(name = "PRICE")
    private Long price;

    @Column(name = "PERIOD")
    private int period;

    @Column(name = "PARTY_NOTICE")
    private String partyNotice;

    @Builder
    public Party(Long partyId, Ott ott, String leaderId, Integer presentHeadcount, Integer maxHeadcount, LocalDateTime regYmd, String ottAccId, String ottAccPw, Integer matchingType, String title, String partyDetail, Long price, int period, String partyNotice) {
        this.partyId = partyId;
        this.ott = ott;
        this.leaderId = leaderId;
        this.presentHeadcount = presentHeadcount;
        this.maxHeadcount = maxHeadcount;
        this.regYmd = regYmd;
        this.ottAccId = ottAccId;
        this.ottAccPw = ottAccPw;
        this.matchingType = matchingType;
        this.title = title;
        this.partyDetail = partyDetail;
        this.price = price;
        this.period = period;
        this.partyNotice = partyNotice;
    }
}
