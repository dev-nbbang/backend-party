package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // 일반 결제 파티 탈퇴(환불 안함)

    // 정기 결제 파티 탈퇴 시 환불 정보 조회(파티 ?)

    // 파티 참가
    Participant save(Participant participant);

    // 파티원 초대

    // 파티원 초대 시 파티 가입 여부 판단
    Participant findByOttIdAndParticipantId(Long ottId, String participantId);

    // 파티원이 파티 탈퇴
    void deleteByPartyAndParticipantId(Party party, String participantId);

    // 파티 해체로 인한 파티 강제 탈퇴
    void deleteByParty(Party party);

    // 파티 해체로 인한 파티 강제 탈퇴 검증
    List<Participant> findAllByParty(Party party);

    // 파티원이 파티 탈퇴 경우 탈퇴 검증
    Participant findByPartyAndParticipantId(Party party, String participantId);

    // 일주일 매칭 인원수 판단
    @Query("SELECT COUNT(p.id) FROM Participant p WHERE p.ottId = :ottId AND p.participantYmd >= :participantYmd")
    Integer matchingCountDuringWeek(@Param("ottId") Long ottId, @Param("participantYmd") LocalDateTime participantYmd);

    // 회원 조회
    @Query("SELECT p FROM Participant p join fetch p.party where p.participantId = :participantId order by p.participantYmd desc")
    List<Participant> findByParticipantId(@Param("participantId") String participantId);
}
