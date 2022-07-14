package com.dev.nbbang.party.domain.party.dto.request;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.party.entity.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PartyCreateRequest {
    private Long ottId;
    private String leaderId;
    private Integer maxHeadcount;
    private String ottAccId;
    private String ottAccPw;
    private Integer matchingType;       // 타입 1 : 정기결제, 타입 2 일반 결제
    private String title;
    private String partyDetail;
    private Long price;
    private Integer period;

    @Builder
    public PartyCreateRequest(Long ottId, String leaderId, Integer maxHeadcount, String ottAccId, String ottAccPw, Integer matchingType, String title, String partyDetail, Long price, Integer period) {
        this.ottId = ottId;
        this.leaderId = leaderId;
        this.maxHeadcount = maxHeadcount;
        this.ottAccId = ottAccId;
        this.ottAccPw = ottAccPw;
        this.matchingType = matchingType;
        this.title = title;
        this.partyDetail = partyDetail;
        this.price = price;
        this.period = period;
    }

    public static Party toEntity(PartyCreateRequest request, String ottAccPw, OttDTO ott) {
        return Party.builder()
                .ott(OttDTO.toEntity(ott))
                .leaderId(request.getLeaderId())
                .maxHeadcount(request.getMaxHeadcount())
                .regYmd(LocalDateTime.now())
                .ottAccId(request.getOttAccId())
                .ottAccPw(ottAccPw)
                .matchingType(request.getMatchingType())
                .title(request.getTitle())
                .partyDetail(request.getPartyDetail())
                .price(request.getPrice())
                .period(request.getPeriod())
                .build();
    }
}
