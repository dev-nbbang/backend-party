package com.dev.nbbang.party.domain.party.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantValidResponse {
    private String participantId;
    private Boolean validJoinParty;

    @Builder
    public ParticipantValidResponse(String participantId, Boolean validJoinParty) {
        this.participantId = participantId;
        this.validJoinParty = validJoinParty;
    }

    public static ParticipantValidResponse create(String participantId, Boolean validJoinParty) {
        return ParticipantValidResponse.builder()
                .participantId(participantId)
                .validJoinParty(validJoinParty)
                .build();
    }
}
