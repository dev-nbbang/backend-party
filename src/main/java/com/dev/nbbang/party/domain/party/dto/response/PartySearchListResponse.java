package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class PartySearchListResponse {
    private Long partyId;
    private Ott ott;
    private String leaderId;
    private Integer presentHeadcount;
    private Integer maxHeadcount;
    private LocalDateTime regYmd;
    private Integer matchingType;
    private String title;
    private Long price;
    private Integer period;

    @Builder
    public PartySearchListResponse(Long partyId, Ott ott, String leaderId, Integer presentHeadcount, Integer maxHeadcount, LocalDateTime regYmd, Integer matchingType, String title, Long price, Integer period) {
        this.partyId = partyId;
        this.ott = ott;
        this.leaderId = leaderId;
        this.presentHeadcount = presentHeadcount;
        this.maxHeadcount = maxHeadcount;
        this.regYmd = regYmd;
        this.matchingType = matchingType;
        this.title = title;
        this.price = price;
        this.period = period;
    }

    public static List<PartySearchListResponse> createList(List<PartyDTO> partyList) {
        List<PartySearchListResponse> response = new ArrayList<>();
        for (PartyDTO party : partyList) {
            response.add(PartySearchListResponse.builder()
                    .partyId(party.getPartyId())
                    .ott(party.getOtt())
                    .leaderId(party.getLeaderId())
                    .presentHeadcount(party.getPresentHeadcount())
                    .maxHeadcount(party.getMaxHeadcount())
                    .regYmd(party.getRegYmd())
                    .matchingType(party.getMatchingType())
                    .title(party.getTitle())
                    .price(party.getPrice())
                    .period(party.getPeriod())
                    .build());

        }
        return response;
    }
}
