package com.dev.nbbang.party.domain.ott.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;

import java.util.List;

public interface OttService {
    // Ott Id로 Ott 서비스 조회
    OttDTO findOtt(Long ottId);

    // Ott 서비스 전체 조회
    List<OttDTO> findAllOtt();

    // Ott 서비스 정보 수정
    OttDTO modifyOtt(Long ottId, Ott ott);

    // Ott 서비스 추가
    OttDTO saveOtt(Ott ott);

    // Ott 서비스 삭제
    void deleteOtt(Long ottId);
}
