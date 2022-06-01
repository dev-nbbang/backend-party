package com.dev.nbbang.party.domain.party.dto.response;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyOttAccResponse {
    private Long partyId;
    private String ottAccId;
    private String ottAccPw;

    @Builder
    public PartyOttAccResponse(Long partyId, String ottAccId, String ottAccPw) {
        this.partyId = partyId;
        this.ottAccId = ottAccId;
        this.ottAccPw = ottAccPw;
    }

    public static PartyOttAccResponse create(PartyDTO party) {
        return PartyOttAccResponse.builder()
                .partyId(party.getPartyId())
                .ottAccId(party.getOttAccId())
                .ottAccPw(party.getOttAccPw())
                .build();
    }
}
