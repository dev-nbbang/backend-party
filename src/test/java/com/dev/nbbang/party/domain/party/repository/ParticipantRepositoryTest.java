package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParticipantRepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("파티원 레포지토리 : 파티원 저장")
    void 파티원_저장() {
        // given
        Participant participant = testParticipant("participant");

        // when
        Participant savedParticipant = participantRepository.save(participant);

        // then
        assertThat(savedParticipant.getParty().getPartyId()).isEqualTo(participant.getParty().getPartyId());
        assertThat(savedParticipant.getParticipantId()).isEqualTo(participant.getParticipantId());
        assertThat(savedParticipant.getOttId()).isEqualTo(participant.getOttId());
    }

    @Test
    @DisplayName("파티원 레포지토리 : OTT ID, 파티원 아이디로 파티원 정보 조회")
    void OTT_ID와_파티원_아이디로_파티원_정보_조회() {
        // given
        String participantId = "participant";
        Long ottId = 1L;
        participantRepository.save(testParticipant("participant"));

        // when
        Participant findParticipant = participantRepository.findByOttIdAndParticipantId(ottId, participantId);

        // then
        assertThat(findParticipant.getParticipantId()).isEqualTo(participantId);
        assertThat(findParticipant.getOttId()).isEqualTo(ottId);
    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티와 파티원 아이디로 파티원 삭제")
    void 파티와_파티원_아이디로_파티원_삭제() {
        // given
        Party party = testParty();
        String participantId = "participant";
        participantRepository.save(testParticipant("participant"));

        // when
        participantRepository.deleteByPartyAndParticipantId(party, participantId);

        // then
        assertThat(participantRepository.findByPartyAndParticipantId(party, participantId)).isNull();
    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티로 파티원 삭제")
    void 파티로_파티원_삭제() {
        // given
        Party party = testParty();
        participantRepository.save(testParticipant("participant"));

        // when
        participantRepository.deleteByParty(party);

        // then
        assertThat(participantRepository.findAllByParty(party)).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티로 파티원 전체 조회")
    void 파티로_파티원_전체_조회() {
        // given
        Party party = testParty();
        participantRepository.save(testParticipant("participant1"));
        participantRepository.save(testParticipant("participant2"));
        participantRepository.save(testParticipant("participant3"));

        // when
        List<Participant> findParticipants = participantRepository.findAllByParty(party);

        // then
        assertThat(findParticipants.size()).isEqualTo(3);
        for (Participant findParticipant : findParticipants) {
            assertThat(findParticipant.getParty().getPartyId()).isEqualTo(party.getPartyId());
        }
    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티와 파티원 아이디로 파티원 찾기")
    void 파티와_파티원_아이디로_파티원_조회() {
        // given
        Party party = testParty();
        String participantId = "participant";
        participantRepository.save(testParticipant(participantId));

        // when
        Participant findParticipant = participantRepository.findByPartyAndParticipantId(party, participantId);

        // then
        assertThat(findParticipant.getParty().getPartyId()).isEqualTo(party.getPartyId());
        assertThat(findParticipant.getParticipantId()).isEqualTo(participantId);
    }

    @Test
    @DisplayName("파티원 레포지토리 : 일주일 매칭 인원수 조회")
    void OTT_ID와_오늘날짜로_일주일_매칭_인원수_조회() {
        Long ottId = 1L;
        participantRepository.save(testParticipant("participant1"));
        participantRepository.save(testParticipant("participant2"));
        participantRepository.save(testParticipant("participant3"));

        // when
        Integer matchingCount = participantRepository.matchingCountDuringWeek(ottId, LocalDateTime.now().minusWeeks(1));

        // then
        assertThat(matchingCount).isEqualTo(3);
    }

    @Test
    @DisplayName("파티원 레포지토리 : 회원의 가입한 파티 목록 조회")
    void 가입한_파티_목록_조회() {
        String participantId = "K-2197723261";

        // when
        List<Participant> findParticipant = participantRepository.findByParticipantId(participantId);

        // then
        assertThat(findParticipant.size()).isEqualTo(3);
        assertAll(
                () -> {
                    assertThat(findParticipant.get(0).getParty().getPartyId()).isEqualTo(6L);
                    assertThat(findParticipant.get(1).getParty().getPartyId()).isEqualTo(2L);
                    assertThat(findParticipant.get(2).getParty().getPartyId()).isEqualTo(3L);
                    assertThat(findParticipant.get(0).getParty().getOtt().getOttId()).isEqualTo(1L);
                    assertThat(findParticipant.get(0).getParty().getOtt().getOttName()).isEqualTo("넷플릭스");
                }
        );
    }

    public static Participant testParticipant(String participantId) {
        return Participant.builder()
                .party(testParty())
                .participantId(participantId)
                .participantYmd(LocalDateTime.now())
                .ottId(1L)
                .build();
    }


    public static Party testParty() {
        return Party.builder()
                .partyId(1L)
                .build();
    }
}