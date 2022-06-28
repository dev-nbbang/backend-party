package com.dev.nbbang.party.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLeaveResponse {
    private String memberId;

    @Builder
    public MemberLeaveResponse(String memberId) {
        this.memberId = memberId;
    }
}
