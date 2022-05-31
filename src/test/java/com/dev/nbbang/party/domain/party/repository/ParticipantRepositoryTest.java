package com.dev.nbbang.party.domain.party.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParticipantRepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("파티원 레포지토리 : 파티원 저장")
    void 파티원_저장() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : OTT ID, 파티원 아이디로 파티원 정보 조회")
    void OTT_ID와_파티원_아이디로_파티원_정보_조회() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티와 파티원 아이디로 파티원 삭제")
    void 파티와_파티원_아이디로_파티원_삭제() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티로 파티원 삭제")
    void 파티로_파티원_삭제() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티로 파티원 전체 조회")
    void 파티로_파티원_전체_조회() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : 파티와 파티원 아이디로 파티원 찾기")
    void 파티와_파티원_아이디로_파티원_조회() {

    }

    @Test
    @DisplayName("파티원 레포지토리 : 일주일 매칭 인원수 조회")
    void OTT_ID와_오늘날짜로_일주일_매칭_인원수_조회() {

    }
}