package com.dev.nbbang.party.domain.ott.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OttRepository extends JpaRepository<Ott, Long> {
    // OTT 플랫폼 아이디로 OTT 플랫폼 정보 가져오기
    Ott findByOttId(Long ottId);
}
