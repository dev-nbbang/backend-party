package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.PartyCreateRequest;
import com.dev.nbbang.party.domain.party.dto.request.PartyModifyRequest;
import com.dev.nbbang.party.domain.party.dto.response.PartyCreateResponse;
import com.dev.nbbang.party.domain.party.dto.response.PartyModifyResponse;
import com.dev.nbbang.party.domain.party.exception.NoCreatePartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.global.common.CommonResponse;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<?> updatePartyInformation(@RequestBody PartyModifyRequest request,  @PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Party Controller - Update Party Information] 파티 제목 및 상세 정보 수정");

        String memberId = servletRequest.getHeader("X-Authorization-Id");

        PartyDTO updatedParty = partyService.updatePartyInformation(partyId, request.getTitle(), request.getPartyDetail(), memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(PartyModifyResponse.create(updatedParty));
    }
}
