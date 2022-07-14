package com.dev.nbbang.party.domain.party.dto.request;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.party.entity.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PartyJoinRequest {
    private Long partyId;
}
