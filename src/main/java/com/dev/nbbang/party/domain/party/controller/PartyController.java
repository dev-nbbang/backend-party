package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.*;
import com.dev.nbbang.party.domain.party.dto.response.*;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
