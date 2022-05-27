package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyNoticeResponse {
    private Long partyId;
    private String partyNotice;

    @Builder
    public PartyNoticeResponse(Long partyId, String partyNotice) {
        this.partyId = partyId;
        this.partyNotice = partyNotice;
    }

    public static PartyNoticeResponse create(PartyDTO party) {
        return PartyNoticeResponse.builder()
                .partyId(party.getPartyId())
                .partyNotice(party.getPartyNotice())
                .build();
    }
}
