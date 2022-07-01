package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.*;
import com.dev.nbbang.party.domain.party.dto.response.*;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.service.ParticipantService;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.global.common.CommonResponse;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/party")
public class PartyController {
    private final PartyService partyService;
    private final OttService ottService;
    private final ParticipantService participantService;

    @PostMapping(value = "/new")
    public ResponseEntity<?> createParty(@RequestBody PartyCreateRequest request, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Create Party] 파티 생성");

        // 회원 아이디 파싱할 필요 있는지 확인

        // Ott 서비스 찾기
        OttDTO findOtt = ottService.findOtt(request.getOttId());

        // 파티 생성
        PartyDTO createdParty = partyService.createParty(PartyCreateRequest.toEntity(request, findOtt));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, PartyCreateResponse.create(createdParty), "파티 생성이 완료되었습니다."));

    }

    @GetMapping(value = "/{partyId}")
    public ResponseEntity<?> findPartyByPartyId(@PathVariable(name = "partyId") Long partyId) {
        log.info("[Party Controller - Find Party By Party Id] 파티 조회");

        // 파티 아이디로 파티 조회하기
        PartyDTO findParty = partyService.findPartyByPartyId(partyId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, findParty, "파티 조회에 성공했습니다."));
    }

    @DeleteMapping(value = "/{partyId}")
    public ResponseEntity<?> deleteParty(@PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Delete Party] 파티 해체");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        partyService.deleteParty(partyId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{partyId}")
    public ResponseEntity<?> updatePartyInformation(@RequestBody PartyModifyRequest request, @PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Update Party Information] 파티 제목 및 상세 정보 수정");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        PartyDTO updatedParty = partyService.updatePartyInformation(partyId, request.getTitle(), request.getPartyDetail(), memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, PartyModifyResponse.create(updatedParty), "파티 정보 수정에 성공했습니다."));
    }

    @GetMapping(value = "/{ottId}/list/all")
    public ResponseEntity<?> findPartyList(@PathVariable(name = "ottId") Long ottId, @RequestParam(name = "partyId") Long partyId, @RequestParam(name = "size") Integer size) {
        log.info("[Party Controller - Find Party List All] 모집 마감이 안된 모든 파티리스트를 조회");

        // ott 조회
        OttDTO findOtt = ottService.findOtt(ottId);

        // 모집 마감 안된 파티 리스트 조회
        List<PartyDTO> findPartyList = partyService.findPartyList(OttDTO.toEntity(findOtt), partyId, size);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, PartySearchListResponse.createList(findPartyList), "파티 리스트 조회에 성공했습니다."));
    }

    @GetMapping(value = "/{ottId}/list/{matchingType}")
    public ResponseEntity<?> findPartyList(@PathVariable(name = "ottId") Long ottId, @PathVariable(name = "matchingType") Integer matchingType,
                                           @RequestParam(name = "partyId") Long partyId, @RequestParam(name = "size") Integer size) {

        log.info("[Party Controller - Find Party List All] 모집 마감이 안된 모든 파티리스트를 조회");

        // ott 조회
        OttDTO findOtt = ottService.findOtt(ottId);

        // 매칭 타입을 통해 마감 안된 파티 리스트 조회
        List<PartyDTO> findPartyList = partyService.findPartyListByMatchingType(matchingType, OttDTO.toEntity(findOtt), partyId, size);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, PartySearchListResponse.createList(findPartyList), "파티 리스트 조회에 성공했습니다."));
    }

    @GetMapping(value = "/ott-acc/validation")
    public ResponseEntity<?> validOttAcc(@RequestBody PartyOttAccRequest request) {
        log.info("[Party Controller - Valid Ott Acc] OTT 계정 중복 등록 검증");

        // ott 조회
        OttDTO findOtt = ottService.findOtt(request.getOttId());

        // OTT 계정 중복 등록 검증
        Boolean validOttAcc = partyService.duplicateOttAcc(OttDTO.toEntity(findOtt), request.getOttAccId());

        return ResponseEntity.ok(CommonSuccessResponse.response(true, PartyOttAccDuplicateResponse.create(validOttAcc), "사용 가능한 OTT 계정 아이디입니다"));
    }

    @PutMapping(value = "{partyId}/notice/{noticeType}")
    public ResponseEntity<?> managePartyNotice(@PathVariable(name = "partyId") Long partyId, @PathVariable(name = "noticeType") NoticeType noticeType,
                                               @RequestBody PartyNoticeRequest request, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Manage Party Notice] 파티장의 파티 공지 관리");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 파티 공지 관리
        PartyDTO updatedParty = partyService.updatePartyNotice(noticeType, partyId, memberId, request.getPartyNotice());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, PartyNoticeResponse.create(updatedParty), "파티 공지 수정에 성공했습니다."));
    }

    @GetMapping(value = "/{partyId}/ott-acc")
    public ResponseEntity<?> searchOttAccInformation(@PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Search Ott Acc Information] 소속 파티의 OTT 계정 조회");

        // 회원 아이디 추출 (파티원이 현재 파티에 소속되어 있는지 검증) -> 파티원 테이블에서 검증
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // OTT 계정 조회
        PartyDTO findParty = partyService.findPartyByPartyId(partyId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, PartyOttAccResponse.create(findParty), "OTT 서비스 계정 정보 조회에 성공했습니다."));
    }

    @PutMapping(value = "/{partyId}/ott-acc")
    public ResponseEntity<?> modifyOttAccInformation(@PathVariable(name = "partyId") Long partyId, @RequestBody PartyOttAccInformationRequest request,
                                                     HttpServletRequest servletRequest) {
        log.info("[Party Controller -Modify Ott Acc Information] OTT 계정 정보 수정");

        // 회원 아이디 추출 (파티장 권한 확인 위해)
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // OTT 계정 정보 수정
        PartyDTO updatedParty = partyService.updateOttAcc(partyId, memberId, request.getOttAccId(), request.getOttAccPw());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, PartyOttAccResponse.create(updatedParty), "OTT 서비스 계정 정보 수정에 성공했습니다."));
    }

    @DeleteMapping(value = "/{partyId}/participant")
    public ResponseEntity<?> leaveParty(@PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Leave Party] 파티원이 파티를 탈퇴");

        // 회원 아이디 파싱
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 파티탈퇴 시작
        participantService.outFromParty(partyId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{ottId}/nickname")
    public ResponseEntity<?> validJoinParty(@PathVariable(name = "ottId") Long ottId, @RequestBody ParticipantValidRequest request) {
        log.info("[Party controller - Valid Join Party] 파티에 가입되어있는 회원 인지 검증");

        // 가입 검증
        Boolean validJoinParty = participantService.validParticipateParty(ottId, request.getParticipantId());

        return ResponseEntity.ok(CommonSuccessResponse.response(true, ParticipantValidResponse.create(request.getParticipantId(), validJoinParty), "파티 초대가 가능한 회원입니다."));
    }

    @GetMapping(value = "{ottId}/matching/week")
    public ResponseEntity<?> countMatchingParticipant(@PathVariable(name = "ottId") Long ottId) {
        log.info("[Party Controller - Count Matching Participant]일주일간 매칭된 회원 수");

        // 인원수 구하기
        Integer matchingCount = participantService.matchingCountForWeek(ottId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, MatchingCountResponse.create(ottId, matchingCount), "매칭 인원수 조회에 성공했습니다."));
    }

    @PostMapping(value = "/join-party")
    public ResponseEntity<?> joinParty(@RequestBody PartyJoinRequest partyJoinRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        Long partyId = partyJoinRequest.getPartyId();
        //파티 인원수 확인 및 파티 가입 가능시 파티 참가자 추가 불가시 false
        if(partyService.isPartyJoin(partyId, memberId)) {
            return ResponseEntity.ok(CommonResponse.response(true, "파티 가입 성공"));
        }
        return ResponseEntity.ok(CommonResponse.response(false, "파티 가입 실패"));
    }
    @PutMapping(value = "/join-party")
    public ResponseEntity<?> rollBackParty(@RequestBody PartyJoinRequest partyJoinRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        Long partyId = partyJoinRequest.getPartyId();
        partyService.isRollBackPartyJoin(partyId, memberId);
        return ResponseEntity.ok(CommonResponse.response(true, "파티 롤백 성공"));
    }

    @PostMapping(value = "/matching-apply")
    public ResponseEntity<?> matchingApply(@RequestBody MathcingApplyRequest MathcingApplyRequest, HttpServletRequest req) {
        String memberId = req.getHeader("X-Authorization-Id");
        Long ottId = MathcingApplyRequest.getOttId();
        String billingKey = MathcingApplyRequest.getBillingKey();
        partyService.setMatching(ottId, billingKey, memberId);
        return ResponseEntity.ok(CommonResponse.response(true, "매칭 대기열 저장"));
    }

    @GetMapping(value = "/test")
    public ResponseEntity<?> test() {
        List<OttDTO> ottDTOList = ottService.findAllOtt();
        for(int i=0; i<ottDTOList.size(); i++) {
            //사이즈만큼 ottid별 현재인원수가 max가 아닌 파티들 불러오기 생성일자 기준 asc
            Ott ott = OttDTO.toEntity(ottDTOList.get(i));
            long ottId = ott.getOttId();
            int maxCount = ott.getOttHeadcount();
            List<PartyDTO> partyDTOList = partyService.findJoinPartyList(ott, maxCount);
            log.info(partyDTOList.size() + " " + ottId);
            //사이즈가 0이면 break
            if(partyDTOList.size() == 0) continue;
            //redis에 해당 ottid list 가져오기
            long matchSize = partyService.getMatchingSize("matching:"+ottId);
            //list 사이즈가 0이면 break
            if(matchSize==0) continue;
            //list에 memberId와 rdb에 partyId로 매칭 (파티추가및수정) 여기서 실패하면 다른 rdb꺼 실행
            partyService.matchingParty(partyDTOList, ott, matchSize);
        }

        return null;
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    public void matchingParty() {
        //ottid 불러오기
        List<OttDTO> ottDTOList = ottService.findAllOtt();
        for(int i=0; i<ottDTOList.size(); i++) {
            //사이즈만큼 ottid별 현재인원수가 max가 아닌 파티들 불러오기 생성일자 기준 asc
            Ott ott = OttDTO.toEntity(ottDTOList.get(i));
            long ottId = ott.getOttId();
            int maxCount = ott.getOttHeadcount();
            List<PartyDTO> partyDTOList = partyService.findJoinPartyList(ott, maxCount);
            //사이즈가 0이면 break
            if(partyDTOList.size() == 0) break;
            //redis에 해당 ottid list 가져오기
            long matchSize = partyService.getMatchingSize("matching:"+ottId);
            //list 사이즈가 0이면 break
            if(matchSize==0) break;

            //list에 memberId와 rdb에 partyId로 매칭 (파티추가및수정) 여기서 실패하면 다른 rdb꺼 실행

            //정기결제 실행 여기서 실패하면 알림으로 실패 사유와 billingkey 수정 요청 및 rollback
        }
    }
}
