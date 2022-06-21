package com.dev.nbbang.party.domain.ott.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OttRepository extends JpaRepository<Ott, Long> {
    // OTT 플랫폼 아이디로 OTT 플랫폼 정보 가져오기
    Ott findByOttId(Long ottId);

    // OTT 서비스 한개 삭제
    void deleteByOttId(Long ottId);

    // OTT 서비스 추가
    Ott save(Ott ott);

    // OTT 서비스 전체 조회
    List<Ott> findAll();

}
