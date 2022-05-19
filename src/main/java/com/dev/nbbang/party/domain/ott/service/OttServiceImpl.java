package com.dev.nbbang.party.domain.ott.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OttServiceImpl implements OttService {
    private final OttRepository ottRepository;

    /**
     * Ott 플랫폼 아이디로 Ott 플랫폼 정보를 불러온다.
     * @param ottId Long 타입의 Ott 플랫폼 아이디
     * @return OttDTO Ott 플랫폼 정보를 담은 데이터
     */
    @Override
    public OttDTO findOtt(Long ottId) {
        // 1. OttId를 이용해 OTT 정보 조회
        Ott findOtt = Optional.ofNullable(ottRepository.findByOttId(ottId)).orElseThrow(() -> new NoSuchOttException("등록되지 않은 Ott 플랫폼 서비스입니다.", NbbangException.NOT_FOUND_OTT));

        return OttDTO.create(findOtt);
    }
}
