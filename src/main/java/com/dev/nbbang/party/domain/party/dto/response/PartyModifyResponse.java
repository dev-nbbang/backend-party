package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyModifyResponse {
    private Long partyId;
    private String title;
    private String partyDetail;

    @Builder
    public PartyModifyResponse(Long partyId, String title, String partyDetail) {
        this.partyId = partyId;
        this.title = title;
        this.partyDetail = partyDetail;
    }

    public static PartyModifyResponse create(PartyDTO party) {
        return PartyModifyResponse.builder()
                .partyId(party.getPartyId())
                .title(party.getTitle())
                .partyDetail(party.getPartyDetail())
                .build();
    }
}
