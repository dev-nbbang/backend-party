package com.dev.nbbang.party.domain.party.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MatchingCountResponse {
    private Long ottId;
    private Integer matchingCount;

    @Builder
    public MatchingCountResponse(Long ottId, Integer matchingCount) {
        this.ottId = ottId;
        this.matchingCount = matchingCount;
    }

    public static MatchingCountResponse create(Long ottId,Integer matchingCount) {
        return MatchingCountResponse.builder()
                .ottId(ottId)
                .matchingCount(matchingCount)
                .build();
    }
}
