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

    @OneToOne(fetch = FetchType.LAZY)
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
    private Integer period;

    @Column(name = "PARTY_NOTICE")
    private String partyNotice;

    @Builder
    public Party(Long partyId, Ott ott, String leaderId, Integer presentHeadcount, Integer maxHeadcount, LocalDateTime regYmd, String ottAccId, String ottAccPw, Integer matchingType, String title, String partyDetail, Long price, Integer period, String partyNotice) {
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

    // 일반 결제 시 파티 정보 수정
    public void updatePartyDetails(String title, String partyDetail) {
        this.title = title;
        this.partyDetail = partyDetail;
    }

    // 파티 공지 작성, 수정
    public void updatePartyNotice(String partyNotice) {
        this.partyNotice = partyNotice;
    }

    // 파티 공지 삭제 -> 공백으로 변경
    public void deletePartyNotice() {
        this.partyNotice = "";
    }

    // OTT 계정 조회
    public void updateOttAcc(String ottAccId, String ottAccPw) {
        this.ottAccId = ottAccId;
        this.ottAccPw = ottAccPw;
    }

    // 파티 현재 인원 수정
    public void decreasePresentHeadCount() {
        if(this.presentHeadcount < 1) return;
        this.presentHeadcount -= 1;
    }

    // 파티 현재 인원 수정
    public void increasePresentHeadcount() {
        if(this.presentHeadcount.intValue() == this.maxHeadcount.intValue()) return;
        this.presentHeadcount += 1;
    }
}
