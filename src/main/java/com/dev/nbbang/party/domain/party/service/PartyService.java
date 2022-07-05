package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.entity.Party;

import java.util.List;
import java.util.Map;

public interface PartyService {
    // 파티 생성
    PartyDTO createParty(Party party);

    // 파티 아이디로 파티 상세 정보 조회
    PartyDTO findPartyByPartyId(Long partyId);

    // 파티 아이디로 파티 해체
    void deleteParty(Long partyId, String leaderId);

    // 파티 정보 수정하기
    PartyDTO updatePartyInformation(Long partyId, String title, String partyDetail, String leaderId);

    // 마감 안된 OTT 파티 리스트 조회
    List<PartyDTO> findPartyList(Ott ott, Long partyId, int size);

    // 마감 안된 OTT + 결제 유형 파티 리스트 조회
    List<PartyDTO> findPartyListByMatchingType(Integer matchingType, Ott ott, Long partyId, int size);

    // OTT 계정 중복 확인
    Boolean duplicateOttAcc(Ott ott, String ottAccId);

    // 일반 결제 팔티 탈퇴(파티원?)

    // 정기 결제 파티 탈퇴(파티원?)

    // 파티 공지 작성, 수정, 삭제
    PartyDTO updatePartyNotice(NoticeType noticeType, Long partyId, String leaderId, String partyNotice);

    // 파티 참가 (파티원)

    // OTT Id/PW 조회 -> findPartyByPartyId

    // OTT Id/PW 수정 (암호화)
    PartyDTO updateOttAcc(Long partyId, String leaderId, String ottAccId, String ottAccPw);

    // 파티원 초대시 파티 가입 여부 판단 (파티원)

    // 일주일 매칭 인원 수 (파티원)

    //파티 가격 정보
    int findPrice(Long partyId);

    //Ott 가격 정보
    OttDTO findOttPrice(Long partyId);

    //환불 로직

    //파티 가입 (LOCK)
    PartyDTO isPartyJoin(Long partyId, String memberId);

    //파티 롤백 (LOCK)
    void isRollBackPartyJoin(Long partyId, String memberId);

    //현재인원수가 max가 아닌 파티들 불러오기 생성일자 기준 asc
    List<PartyDTO> findJoinPartyList(Ott ott, int maxHeadCount);

    //매칭 대기열 사이즈
    long getMatchingSize(String key);

    //파티와 사용자 매칭
    void matchingParty(List<PartyDTO> partyDTOList, Ott ott, long size);

    //매칭 지원
    void setMatching(long ottId, String billingKey, String memberId);

//    void autoMatching(Party party);

    List<String> matchingList(String memberId);

    void deleteMatchingList(List<String> ottIds, String memberId);

    void changeBilling(String billingKeyEnc, String memberId);
}
