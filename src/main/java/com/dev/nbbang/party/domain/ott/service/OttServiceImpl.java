package com.dev.nbbang.party.domain.ott.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.exception.FailDeleteOttException;
import com.dev.nbbang.party.domain.ott.exception.NoCreateOttException;
import com.dev.nbbang.party.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OttServiceImpl implements OttService {
    private final OttRepository ottRepository;

    /**
     * Ott 플랫폼 아이디로 Ott 플랫폼 정보를 불러온다.
     *
     * @param ottId Long 타입의 Ott 플랫폼 아이디
     * @return OttDTO Ott 플랫폼 정보를 담은 데이터
     */
    @Override
    public OttDTO findOtt(Long ottId) {
        // 1. OttId를 이용해 OTT 정보 조회
        Ott findOtt = Optional.ofNullable(ottRepository.findByOttId(ottId)).orElseThrow(() -> new NoSuchOttException("등록되지 않은 Ott 플랫폼 서비스입니다.", NbbangException.NOT_FOUND_OTT));

        return OttDTO.create(findOtt);
    }

    /**
     * 등록된 모든 Ott 플랫폼 정보를 조회한다.
     *
     * @return List 타입의 Ott 정보
     */
    @Override
    public List<OttDTO> findAllOtt() {
        // 1. 모든 OTT 조회
        List<Ott> findOttList = ottRepository.findAll();
        if (findOttList.isEmpty()) throw new NoSuchOttException("현재 등록된 Ott 플랫폼이 없습니다.", NbbangException.NOT_FOUND_OTT);

        return OttDTO.createList(findOttList);
    }

    /**
     * Ott 플랫폼 정보를 수정한다.
     *
     * @param ott 수정될 Ott 플랫폼 정보
     * @return 수정된 Ott 플랫폼 정보
     */
    @Override
    public OttDTO modifyOtt(Long ottId, Ott ott) {
        // 1. Ott 서비스 조회
        Ott updatedOtt = Optional.ofNullable(ottRepository.findByOttId(ottId))
                .orElseThrow(() -> new NoSuchOttException("등록되지 않은 Ott 플랫폼 서비스입니다.", NbbangException.NOT_FOUND_OTT));

        // 2. Ott 서비스 수정
        updatedOtt.updateOtt(ott.getOttName(), ott.getOttHeadcount(), ott.getOttPrice(), ott.getOttImage());

        return OttDTO.create(updatedOtt);
    }

    /**
     * Ott 플랫폼 정보를 추가한다.
     *
     * @param ott 추가할 Ott 플랫폼 정보
     * @return 추가된 Ott 플랫폼 정보
     */
    @Override
    public OttDTO saveOtt(Ott ott) {
        // 1. Ott 서비스를 등록한다.
        Ott savedOtt = Optional.of(ottRepository.save(ott))
                .orElseThrow(() -> new NoCreateOttException("Ott 플랫폼 등록에 실패했습니다.", NbbangException.NO_CREATE_OTT));

        return OttDTO.create(savedOtt);
    }

    /**
     * Ott 아이디로 Ott 플랫폼을 삭제한다.
     *
     * @param ottId 고유한 Ott 플랫폼 아이디
     */
    @Override
    public void deleteOtt(Long ottId) {
        // 1. Ott Id로 플랫폼 정보 삭제
        ottRepository.deleteByOttId(ottId);

        // 2. 제대로 삭제되었는지 검증
        Optional.ofNullable(ottRepository.findByOttId(ottId)).ifPresent(
                exception -> {
                    throw new FailDeleteOttException("Ott 플랫폼 삭제에 실패했습니다", NbbangException.FAIL_TO_DELETE_QNA);
                }
        );
    }
}
