package com.dev.nbbang.party.domain.party.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyOttAccDuplicateResponse {
    private Boolean validOttAcc;

    @Builder
    public PartyOttAccDuplicateResponse(Boolean validOttAcc) {
        this.validOttAcc = validOttAcc;
    }

    public static PartyOttAccDuplicateResponse create(Boolean validOttAcc) {
        return PartyOttAccDuplicateResponse.builder()
                .validOttAcc(validOttAcc)
                .build();
    }
}
