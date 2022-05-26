package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.entity.Party;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    // 파티 저장
    Party save(Party party);

    // 파티 아이디로 파티 정보 조회
    Party findByPartyId(Long partyId);

    // 파티 삭제
    void deleteByPartyId(Long partyId);

    // OTT 별 파티 리스트 전체 조회(나중에 페이징)
    @Query("SELECT p FROM Party p WHERE p.ott = :ott AND p.presentHeadcount < :maxHeadcount")
    Slice<Party> findPartyList(Ott ott, Integer maxHeadcount, Pageable pageable);

    // 결제 유형별 파티 리스트 조회 (나중에 페이징)
    @Query("SELECT p FROM Party p WHERE p.matchingType = :matchingType AND p.ott = :ott AND p.presentHeadcount < :maxHeadcount")
    Slice<Party> findPartyList(Integer matchingType, Ott ott, Integer maxHeadcount, Pageable pageable);

    // OTT 계정 중복 확인
    Party findByOttAndOttAccId(Ott ott, String ottAccId);

}
