package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.entity.Party;

public interface PartyService {
    PartyDTO createParty(Party party);

    PartyDTO findPartyByPartyId(Long partyId);

    int findPrice(Long partyId);
}
