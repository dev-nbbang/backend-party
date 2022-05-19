package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.PartyCreateRequest;
import com.dev.nbbang.party.domain.party.dto.response.PartyCreateResponse;
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

        try {
            // 회원 아이디 파싱할 필요 있는지 확인

            // Ott 서비스 찾기
            OttDTO findOtt = ottService.findOtt(request.getOttId());

            // 파티 생성
            PartyDTO createdParty = partyService.createParty(PartyCreateRequest.toEntity(request, findOtt));

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, PartyCreateResponse.create(createdParty), "파티 생성이 완료되었습니다."));
        } catch (NoCreatePartyException e) {
            log.warn("[Party Controller - Create Party] message : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.response(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/{partyId}")
    public ResponseEntity<?> findPartyByPartyId(@PathVariable(name = "partyId") Long partyId) {
        log.info("[Party Controller - Find Party By Party Id] 파티 조회");

        try {
            // 파티 아이디로 파티 조회하기
            PartyDTO findParty = partyService.findPartyByPartyId(partyId);

            return ResponseEntity.ok(CommonSuccessResponse.response(true, findParty, "파티 조회에 성공했습니다."));
        } catch (NoSuchPartyException e) {
            log.warn("[Party Controller - Find Party By Party Id] message : " + e.getMessage());

            return ResponseEntity.ok(CommonResponse.response(false, e.getMessage()));
        }
    }
}
