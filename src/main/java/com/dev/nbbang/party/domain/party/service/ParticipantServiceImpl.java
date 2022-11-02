package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.party.dto.response.ParticipantPartyResponse;
import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.AlreadyJoinPartyException;
import com.dev.nbbang.party.domain.party.exception.NoJoinPartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchParticipantException;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.repository.ParticipantRepository;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final PartyRepository partyRepository;

    /**
     * 파티원 자신이 파티를 탈퇴한다.
     *
     * @param partyId       고유한 파티 아이디
     * @param participantId 파티원 아이디
     */
    @Override
    @Transactional
    public void outFromParty(Long partyId, String participantId) {
        // 1. 현재 참가중인 파티 불러오기
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        // 2. 파티원 테이블에서 삭제
        Optional.ofNullable(participantRepository.findByPartyAndParticipantId(findParty, participantId))
                .ifPresentOrElse(
                        participant -> {
                            participantRepository.deleteByPartyAndParticipantId(findParty, participantId);
                        },
                        () -> {
                            throw new NoSuchParticipantException("해당 파티의 파티원이 아닙니다.", NbbangException.NOT_FOUND_PARTICIPANT);
                        }
                );

        // 3. 파티 테이블 업데이트 (현재 인원수 한명 감소)
        findParty.decreasePresentHeadCount();

        // 4. 매칭 테이블 넣어줘야하는지 판단 (정기결제경우)
    }

    /**
     * 초대한 파티원이 파티에 이미 가입되어 있는지 판단한다.
     *
     * @param ottId         고유한 OTT 플랫폼 서비스 아이디
     * @param participantId 초대한 파티원 아이디
     * @return 파티원 파티 가입 여부
     */
    @Override
    public Boolean validParticipateParty(Long ottId, String participantId) {
        // 1. 파티원 찾기
        Optional.ofNullable(participantRepository.findByOttIdAndParticipantId(ottId, participantId))
                .ifPresent(
                        exception -> {
                            throw new AlreadyJoinPartyException("이미 파티에 가입되어있습니다.", NbbangException.ALREADY_JOIN_PARTY);
                        }
                );

        return true;
    }

    /**
     * 일주일간 해당 OTT 서비스에 몇명이 매칭되었는지 인원수를 구한다.
     *
     * @param ottId 고유한 OTT 플랫폼 서비스 아이디
     * @return 일주일간 매칭된 인원 수
     */
    @Override
    public Integer matchingCountForWeek(Long ottId) {
        return participantRepository.matchingCountDuringWeek(ottId, LocalDateTime.now().minusWeeks(1L));
    }

    /**
     * 자신이 속한 파티 리스트 조회
     *
     * @param participantId 파티원 아이디
     * @return 자신이 속한 파티 정보
     */
    @Override
    public List<ParticipantPartyResponse> findMyParty(String participantId) {
        List<Participant> findPartyList = participantRepository.findByParticipantId(participantId);

        // 가입된 파티가 없는 경우
        if(findPartyList.isEmpty())
            throw new NoJoinPartyException("가입된 파티가 존재하지 않습니다.", NbbangException.NO_JOIN_PARTY);

        return findPartyList.stream()
                .map(participant -> ParticipantPartyResponse.create(participant.getParty()))
                .collect(Collectors.toList());
    }
}
