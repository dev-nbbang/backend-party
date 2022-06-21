package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyNoticeRequest {
    private String partyNotice;

    @Builder
    public PartyNoticeRequest(String partyNotice) {
        this.partyNotice = partyNotice;
    }
}
