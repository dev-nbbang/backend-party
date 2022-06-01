package com.dev.nbbang.party.domain.ott.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.dto.request.OttInformationRequest;
import com.dev.nbbang.party.domain.ott.dto.response.OttInformationResponse;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/ott")
public class OttController {
    private final OttService ottService;

    // 엔빵 Ott 서비스 전체 조회
    @GetMapping(value = "/list")
    public ResponseEntity<?> searchAllOtt() {
        log.info("[Ott Controller Search All Ott Platform]");

        // Ott 리스트 조회 호출
        List<OttDTO> findOttList = ottService.findAllOtt();

        return ResponseEntity.status(HttpStatus.OK).body(CommonSuccessResponse.response(true, OttInformationResponse.createList(findOttList), "모든 Ott 플랫폼 서비스 조회에 성공했습니다."));
    }

    @GetMapping(value = "/{ottId}")
    public ResponseEntity<?> searchParticularOtt(@PathVariable(name = "ottId") Long ottId) {
        log.info("[Ott Controller Search Particular Ott Platform]");

        // 특정 Ott 플랫폼 서비스 조회
        OttDTO findOtt = ottService.findOtt(ottId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonSuccessResponse.response(true, OttInformationResponse.create(findOtt), "특정 Ott 플랫폼 서비스 조회에 성공했습니다."));
    }

    @PostMapping(value = "/new")
    public ResponseEntity<?> registerOtt(@RequestBody OttInformationRequest request) {
        log.info("[Ott Controller Register Ott Platform]");

        // 특정 Ott 플랫폼 등록
        OttDTO savedOtt = ottService.saveOtt(OttInformationRequest.toEntity(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, OttInformationResponse.create(savedOtt), "특정 Ott 플랫폼 서비스 등록에 성공했습니다"));
    }

    @PutMapping(value = "/{ottId}")
    public ResponseEntity<?> modifyOtt(@PathVariable(name = "ottId") Long ottId, @RequestBody OttInformationRequest request) {
        log.info("[Ott Controller Modify OTt platform]");

        // 특정 Ott 플랫폼 수정
        OttDTO modifyOtt = ottService.modifyOtt(ottId, OttInformationRequest.toEntity(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, OttInformationResponse.create(modifyOtt), "특정 Ott 플랫폼 서비스 수정에 성공했습니다."));
    }

    @DeleteMapping(value = "/{ottId}")
    public ResponseEntity<?> deleteOtt(@PathVariable(name = "ottId") Long ottId) {
        log.info("[Ott Controller Delete Ott Platform]");

        // 특정 Ott 플랫폼 삭제
        ottService.deleteOtt(ottId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

