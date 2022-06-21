package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyOttAccRequest {
    private Long ottId;
    private String ottAccId;

    @Builder
    public PartyOttAccRequest(Long ottId, String ottAccId) {
        this.ottId = ottId;
        this.ottAccId = ottAccId;
    }
}
