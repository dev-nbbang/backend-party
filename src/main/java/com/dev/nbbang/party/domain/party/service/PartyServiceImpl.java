package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.*;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;
    private final QnaRepository qnaRepository;
    private final OttRepository ottRepository;

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

    /**
     * 파티장이 파티를 해체한다.
     * @param partyId 고유한 파티 아이디
     * @param leaderId 파티장 아이디
     */
    @Override
    @Transactional
    public void deleteParty(Long partyId, String leaderId) {
        // 2. 파티 권한 확인 후 파티 찾기
        Party findParty = validationLeader(partyId, leaderId);

        // 3. 파티원에게 파티 해체 알림

        // 4. 파티원 환불 처리

        // 5. 파티원 테이블 삭제

        // 6. QNA 테이블 삭제
        qnaRepository.deleteByParty(findParty);

        // 6-1. QNA 삭제 확인
        List<Qna> findQnaList = qnaRepository.findAllByParty(findParty);
        if(!findQnaList.isEmpty())
            throw new FailDeleteQnaException("파티 질문 내역 삭제에 실패했습니다", NbbangException.FAIL_TO_DELETE_QNA);

        // 7. 파티 테이블 삭제
        partyRepository.deleteByPartyId(partyId);

        // 7-1. 파티 테이블 삭제 확인
        Optional.ofNullable(partyRepository.findByPartyId(partyId)).ifPresent(
                exception -> {
                    throw new FailDeletePartyException("파티 해체에 실패했습니다.", NbbangException.FAIL_TO_DELETE_PARTY);
                }
        );
    }

    /**
     * 파티장이 파티 정보를 수정한다 (일반 결제만 해당)
     * @param partyId 고유 파티 아이디
     * @param title 파티 제목
     * @param partyDetail 파티 상세 내용
     * @param leaderId 파티장 아이디
     * @return 수정된 파티 정보
     */
    @Override
    public PartyDTO updatePartyInformation(Long partyId, String title, String partyDetail, String leaderId) {
        // 1. 파티장 권한 확인 후 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        // 2. 파티 타이틀, 파티 정보 수정
        updatedParty.updatePartyDetails(title, partyDetail);

        return PartyDTO.create(updatedParty);
    }

    /**
     * 마감안된 파티 리스트를 조회한다.
     * @param ott OTT 서비스
     * @param size 한번에 조회할 사이즈
     * @return 마감 안된 파티 리스트
     */
    @Override
    public List<PartyDTO> findPartyList(Ott ott, int size) {
        // 1. 마감 안된 파티 리스트 조회하기
        Slice<Party> findPartyList = partyRepository.findPartyList(ott, ott.getOttHeadcount(), PageRequest.of(0, size));

        if(findPartyList.isEmpty())
            throw new NoSuchPartyException("모집중인 파티가 없습니다.", NbbangException.NOT_FOUND_PARTY);

        return PartyDTO.createList(findPartyList);
    }

    /**
     * 매칭 타입을 통해 마감안된 파티 리스트를 조회한다.
     * @param matchingType 매칭 타입
     * @param ott OTT 서비스
     * @param size 한번에 조회할 사이즈
     * @return 마감 안된 파티 리스트
     */
    @Override
    public List<PartyDTO> findPartyListByMatchingType(Integer matchingType, Ott ott, int size) {
        // 1. 마감 안된 파티 리스트 조회하기
        Slice<Party> findPartyList = partyRepository.findPartyList(matchingType, ott, ott.getOttHeadcount(), PageRequest.of(0, size));

        if(findPartyList.isEmpty())
            throw new NoSuchPartyException("모집중인 파티가 없습니다.", NbbangException.NOT_FOUND_PARTY);

        return PartyDTO.createList(findPartyList);
    }

    /**
     * OTT 계정이 중복되었는지 판단한다.
     * @param ott OTT 서비스
     * @param ottAccId OTT 계정 아이디
     * @return  true/false
     */
    @Override
    public Boolean duplicateOttAcc(Ott ott, String ottAccId) {
        // 1. OTT 계정 중복 확인
        Optional.ofNullable(partyRepository.findByOttAndOttAccId(ott, ottAccId)).ifPresent(
                exception -> {
                    throw new DuplicateOttAccException("중복된 Ott 계정입니다.", NbbangException.DUPLICATE_OTT_ACC);
                }
        );

        return true;
    }

    /**
     * 파티장이 파티 공지를 작성, 수정, 삭제한다.
     * @param noticeType 파티 공지 타입 (작성, 수정, 삭제)
     * @param partyId 고유 파티 아이디
     * @param leaderId 파티장 아이디
     * @param partyNotice 파티 공지 내용
     * @return 수정된 파티 정보
     */
    @Override
    public PartyDTO updatePartyNotice(NoticeType noticeType, Long partyId, String leaderId, String partyNotice) {
        // 1. 파티장 권한 확인 및 업데이트 할 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        // 2. 파티 공지 업데이트
        updatedParty.updatePartyNotice(partyNotice);

        return PartyDTO.create(updatedParty);
    }

    /**
     * 파티장이 OTT 계정을 수정한다.
     * @param partyId 고유 파티 아이디
     * @param leaderId 파티장 아이디
     * @param ottAccId OTT 계정 아이디
     * @param ottAccPw OTT 계정 패스워드
     * @return 수정된 파티 정보
     */
    @Override
    public PartyDTO updateOttAcc(Long partyId, String leaderId, String ottAccId, String ottAccPw) {
        // 1. 파티장 권한 확인 및 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        updatedParty.updateOttAcc(ottAccId, ottAccPw);

        return PartyDTO.create(updatedParty);
    }

    // 파티장 검증
    private Party validationLeader(Long partyId, String leaderId) {
        // 1. 파티 찾기
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        // 2. 파티장 권한 확인
        if(!findParty.getLeaderId().equals(leaderId))
            throw new InvalidLeaderGrantException("파티를 해체할 권한이 없습니다.", NbbangException.INVALID_LEADER_GRANT);
        return findParty;
    }
}
