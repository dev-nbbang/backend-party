package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.NoCreatePartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;

    /**
     * 파티장이 새로운 파티를 생성한다.
     * @param party 새로운 파티 생성 데이터
     * @return PartyDTO 저장된 파티 데이터 정보
     */
    @Override
    @Transactional
    public PartyDTO createParty(Party party) {
        // 추가 검증 로직 필요 시 추가

        // 1. 파티 생성
        Party createdParty = Optional.of(partyRepository.save(party)).orElseThrow(() -> new NoCreatePartyException("파티 생성에 실패했습니다.", NbbangException.NO_CREATE_PARTY));

        return PartyDTO.create(createdParty);
    }

    /**
     * 파티 아이디를 이용해 파티 정보를 조회한다.
     * @param partyId 고유 파티 아이디
     * @return PartyDTO 조회한 파티 데이터 정보
     */
    @Override
    public PartyDTO findPartyByPartyId(Long partyId) {
        // 1. 고유한 파티 아이디로 파티 정보 조회
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId)).orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        return PartyDTO.create(findParty);
    }
}
