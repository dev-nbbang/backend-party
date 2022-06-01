package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.AlreadyJoinPartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchParticipantException;
import com.dev.nbbang.party.domain.party.repository.ParticipantRepository;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private PartyRepository partyRepository;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    @Test
    @DisplayName("파티원 서비스 : 파티원 자의로 파티 탈퇴 성공")
    void 파티원_자의로_파티탈퇴_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testParty());
        given(participantRepository.findByPartyAndParticipantId(any(), anyString())).willReturn(testParticipant("participant"));

        // when
        participantService.outFromParty(2L, "participant");

        // then
        verify(participantRepository, times(1)).deleteByPartyAndParticipantId(any(), anyString());
    }

    @Test
    @DisplayName("파티원 서비스 : 파티원 자의로 파티 탈퇴 실패")
    void 파티원_자의로_파티탈퇴_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testParty());
        given(participantRepository.findByPartyAndParticipantId(any(), anyString())).willThrow(NoSuchParticipantException.class);

        // then
        assertThrows(NoSuchParticipantException.class, () -> participantService.outFromParty(2L, "participant"));
    }

    @Test
    @DisplayName("파티원 서비스 : 파티원 파티 가입 여부 조회 성공")
    void 파티원_파티_가입_여부_조회_성공() {
        // given
        given(participantRepository.findByOttIdAndParticipantId(anyLong(), anyString())).willReturn(null);

        // when
        Boolean validJoinParty = participantService.validParticipateParty(1L, "participant");

        // then
        Assertions.assertThat(validJoinParty).isTrue();
    }

    @Test
    @DisplayName("파티원 서비스 : 파티원 파티 가입 여부 조회 실패")
    void 파티원_파티_가입_여부_조회_실패() {
        // given
        given(participantRepository.findByOttIdAndParticipantId(anyLong(), anyString())).willReturn(testParticipant("participant"));

        // then
        assertThrows(AlreadyJoinPartyException.class, () -> participantService.validParticipateParty(1L, "participant"));
    }

    @Test
    @DisplayName("파티원 서비스 : 일주일간 파티 매칭 수 조회 성공")
    void 일주일_파티_매칭수_조회_성공() {
        // given
        given(participantRepository.matchingCountDuringWeek(anyLong(), any())).willReturn(3);

        // when
        Integer matchingCount = participantService.matchingCountForWeek(1L);

        // then
        Assertions.assertThat(matchingCount).isEqualTo(3);
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
                .presentHeadcount(2)
                .build();
    }

    public static List<Participant> testParticipantList() {
        List<Participant> participants = new ArrayList<>();
        participants.add(testParticipant("participant1"));
        participants.add(testParticipant("participant2"));
        participants.add(testParticipant("participant3"));

        return participants;

    }
}