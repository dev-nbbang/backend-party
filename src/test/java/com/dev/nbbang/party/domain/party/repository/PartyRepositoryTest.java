package com.dev.nbbang.party.domain.party.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.entity.Party;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PartyRepositoryTest {
    @Autowired
    private PartyRepository partyRepository;

    @Test
    @DisplayName("파티 레포지토리 : 파티 정보 저장 성공")
    void 파티_저장_성공() {
        // given
        Party party = testPartyBuilder(13L);

        // when
        Party savedParty = partyRepository.save(party);

        // then
        assertThat(party.getOttAccId()).isEqualTo(savedParty.getOttAccId());
        assertThat(party.getOttAccPw()).isEqualTo(savedParty.getOttAccPw());
        assertThat(party.getOtt().getOttId()).isEqualTo(savedParty.getOtt().getOttId());
    }

    @Test
    @DisplayName("파티 레포지토리 : 파티 아이디로 파티 정보 조회 성공")
    void 파티_정보_조회_성공() {
        // given
        Party savedParty = partyRepository.save(testPartyBuilder(1L));

        // when
        Party findParty = partyRepository.findByPartyId(savedParty.getPartyId());

        // then
        assertThat(findParty.getPartyId()).isEqualTo(savedParty.getPartyId());
        assertThat(findParty.getOtt().getOttId()).isEqualTo(savedParty.getOtt().getOttId());
        assertThat(findParty.getOttAccId()).isEqualTo(savedParty.getOttAccId());
        assertThat(findParty.getOttAccPw()).isEqualTo(savedParty.getOttAccPw());
    }

    @Test
    @DisplayName("파티 레포지토리 : 파티 정보 삭제 성공")
    void 파티_정보_삭제_성공() {
        // given
        Party savedParty = partyRepository.save(testPartyBuilder(1L));

        // when
        partyRepository.deleteByPartyId(1L);
        Party findParty = partyRepository.findByPartyId(1L);

        assertThat(findParty).isNull();
    }

    @Test
    @DisplayName("파티 레포지토리 : 모집 안된 OTT 파티 리스트 조회 성공")
    void 모집_안된_OTT_파티리스트_조회_성공() {
        // given
        Ott ott = testOttBuilder();
        partyRepository.save(testPartyBuilder(100L));
        partyRepository.save(testPartyBuilder(101L));
        partyRepository.save(testPartyBuilder(102L));

        // when
        Slice<Party> findPartyList = partyRepository.findPartyList(ott, ott.getOttHeadcount(), 200L, PageRequest.of(0, 2));

        // then
        assertThat(findPartyList.getSize()).isEqualTo(2);

    }

    @Test
    @DisplayName("파티 레포지토리 : 결제 유형별 파티 리스트 조회 성공")
    void 결제_유형별_파티리스트_조회_성공() {
        // given
        Ott ott = testOttBuilder();
        Integer matchingType = 1;
        partyRepository.save(testPartyBuilder(200L));
        partyRepository.save(testPartyBuilder(201L));
        partyRepository.save(testPartyBuilder(202L));
        partyRepository.save(testPartyBuilder(203L));

        // when
        Slice<Party> findPartyList = partyRepository.findPartyList(matchingType, ott, ott.getOttHeadcount(), 300L, PageRequest.of(0, 2));

        // then
        assertThat(findPartyList.getSize()).isEqualTo(2);

    }

    @Test
    @DisplayName("파티 레포지토리 : OTT 계정 중복 확인 성공")
    void OTT_계정_중복_확인_성공() {
        // given
        Party savedParty = partyRepository.save(testPartyBuilder(1L));
        Ott ott = testOttBuilder();
        String ottAccId = "zayson";

        // when
        Party findOttAcc = partyRepository.findByOttAndOttAccId(ott, ottAccId);

        // then
        assertThat(findOttAcc).isNotNull();
    }

    private static Ott testOttBuilder() {
        return Ott.builder()
                .ottId(1L)
                .ottName("test")
                .ottHeadcount(4)
                .ottPrice(3000L)
                .ottImage("test.image")
                .build();
    }

    private static Party testPartyBuilder(Long ottId) {
        return Party.builder()
                .partyId(ottId)
                .ott(testOttBuilder())
                .leaderId("leader")
                .presentHeadcount(1)
                .maxHeadcount(4)
                .regYmd(LocalDateTime.now())
                .ottAccId("zayson")
                .ottAccPw("1234")
                .matchingType(1)
                .title("title")
                .price(3000L)
                .period(30).build();
    }
}