package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    // 파티 저장
    Party save(Party party);

    // 파티 아이디로 파티 정보 조회
    Party findByPartyId(Long partyId);

}
