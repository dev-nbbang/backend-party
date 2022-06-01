package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyOttAccInformationRequest {
    private String ottAccId;
    private String ottAccPw;

    @Builder
    public PartyOttAccInformationRequest(String ottAccId, String ottAccPw) {
        this.ottAccId = ottAccId;
        this.ottAccPw = ottAccPw;
    }
}
