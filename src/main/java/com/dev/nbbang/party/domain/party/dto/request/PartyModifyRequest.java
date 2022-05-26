package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyModifyRequest {
    private String title;
    private String partyDetail;

    @Builder
    public PartyModifyRequest(String title, String partyDetail) {
        this.title = title;
        this.partyDetail = partyDetail;
    }


}
