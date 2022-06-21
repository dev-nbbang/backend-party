package com.dev.nbbang.party.domain.party.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantValidRequest {
    private String participantId;

    @Builder
    public ParticipantValidRequest(String participantId) {
        this.participantId = participantId;
    }
}
