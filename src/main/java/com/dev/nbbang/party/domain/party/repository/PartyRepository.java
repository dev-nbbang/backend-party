package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.entity.Party;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    // 파티 저장
    Party save(Party party);

    // 파티 아이디로 파티 정보 조회
    Party findByPartyId(Long partyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Party p WHERE p.partyId = :partyId")
    Party findLockPartyId(Long partyId);

    // 파티 삭제
    void deleteByPartyId(Long partyId);

    // OTT 별 파티 리스트 전체 조회(나중에 페이징)
    @Query("SELECT p FROM Party p WHERE p.ott = :ott AND p.presentHeadcount < :maxHeadcount AND p.partyId < :partyId ORDER BY p.regYmd DESC")
    Slice<Party> findPartyList(@Param("ott") Ott ott, @Param("maxHeadcount") Integer maxHeadcount, @Param("partyId") Long partyId, Pageable pageable);

    // 결제 유형별 파티 리스트 조회 (나중에 페이징)
    @Query("SELECT p FROM Party p WHERE p.matchingType = :matchingType AND p.ott = :ott AND p.presentHeadcount < :maxHeadcount AND p.partyId < :partyId ORDER BY p.regYmd DESC")
    Slice<Party> findPartyList(@Param("matchingType") Integer matchingType, @Param("ott") Ott ott, @Param("maxHeadcount") Integer maxHeadcount, @Param("partyId") Long partyId,  Pageable pageable);

    // OTT 계정 중복 확인
    Party findByOttAndOttAccId(Ott ott, String ottAccId);

    //현재인원수가 max가 아닌 파티들 불러오기 생성일자 기준 asc
    @Query("SELECT p FROM Party p WHERE p.ott = :ott AND p.presentHeadcount < :maxHeadCount AND p.matchingType = :matchingType ORDER BY p.regYmd")
    List<Party> findAutoJoinPartyList(Ott ott, int maxHeadCount, int matchingType);

}
