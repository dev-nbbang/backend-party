package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class PartyCreateResponse {
    private Long partyId;
    private Ott ott;
    private String leaderId;
    private Integer presentHeadcount;
    private Integer maxHeadcount;
    private LocalDateTime regYmd;
    private String ottAccId;
    private String ottAccPw;
    private Integer matchingType;
    private String title;
    private String partyDetail;
    private Long price;
    private Integer period;
    private String partyNotice;

    @Builder
    public PartyCreateResponse(Long partyId, Ott ott, String leaderId, Integer presentHeadcount, Integer maxHeadcount, LocalDateTime regYmd, String ottAccId, String ottAccPw, Integer matchingType, String title, String partyDetail, Long price, Integer period, String partyNotice) {
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

    @Builder
    public static PartyCreateResponse create(PartyDTO party) {
        return PartyCreateResponse.builder()
                .partyId(party.getPartyId())
                .ott(party.getOtt())
                .leaderId(party.getLeaderId())
                .presentHeadcount(party.getPresentHeadcount())
                .maxHeadcount(party.getMaxHeadcount())
                .regYmd(party.getRegYmd())
                .ottAccId(party.getOttAccId())
                .ottAccPw(party.getOttAccPw())
                .matchingType(party.getMatchingType())
                .title(party.getTitle())
                .partyDetail(party.getPartyDetail())
                .price(party.getPrice())
                .period(party.getPeriod())
                .partyNotice(party.getPartyNotice())
                .build();
    }
}
