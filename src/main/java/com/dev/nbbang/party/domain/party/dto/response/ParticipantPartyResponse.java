package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.entity.Party;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ParticipantPartyResponse {
    private String leaderId;
    private Integer matchingType;
    private Integer maxHeadcount;
    private OttDTO ott;
    private Long partyId;
    private Integer period;
    private Integer presentHeadcount;
    private Long price;

    private ParticipantPartyResponse(String leaderId, Integer matchingType, Integer maxHeadcount, OttDTO ott, Long partyId, Integer period, Integer presentHeadcount, Long price) {
        this.leaderId = leaderId;
        this.matchingType = matchingType;
        this.maxHeadcount = maxHeadcount;
        this.ott = ott;
        this.partyId = partyId;
        this.period = period;
        this.presentHeadcount = presentHeadcount;
        this.price = price;
    }

    public static ParticipantPartyResponse create(Party party) {
        return new ParticipantPartyResponse(
                party.getLeaderId(),
                party.getMatchingType(),
                party.getMaxHeadcount(),
                OttDTO.create(party.getOtt()),
                party.getPartyId(),
                party.getPeriod(),
                party.getPresentHeadcount(),
                party.getPrice()
        );
    }
}
