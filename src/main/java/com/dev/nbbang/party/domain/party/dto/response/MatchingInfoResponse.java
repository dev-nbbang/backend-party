package com.dev.nbbang.party.domain.party.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MatchingInfoResponse {
    private Long partyId;
    private String memberId;

    @Builder
    public MatchingInfoResponse(Long partyId, String memberId) {
        this.partyId = partyId;
        this.memberId = memberId;
    }

    public static MatchingInfoResponse create(Long partyId, String memberId) {
        return MatchingInfoResponse.builder()
                .partyId(partyId)
                .memberId(memberId)
                .build();
    }
}
