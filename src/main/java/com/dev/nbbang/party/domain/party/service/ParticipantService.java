package com.dev.nbbang.party.domain.party.service;

public interface ParticipantService {
    // 일반 결제 파티 탈퇴 (환불 안해줌)
    void outFromParty(Long partyId, String participantId);

    // 정기 결제 파티탈퇴 정보 조회

    // 파티원 초대

    // 파티원 초대 시 파티 가입 여부 판단
    Boolean validParticipateParty(Long ottId, String participantId);

    // 일주일 매칭 인원수 판단
    Integer matchingCountForWeek(Long ottId);
}
