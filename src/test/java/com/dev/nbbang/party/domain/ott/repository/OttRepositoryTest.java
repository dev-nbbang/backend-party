package com.dev.nbbang.party.domain.ott.repository;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OttRepositoryTest {
    @Autowired
    private OttRepository ottRepository;

    @Test
    @DisplayName("Ott 레포지토리 : Ott 플랫폼 전체 조회 성공")
    void OTT_플랫폼_전체_조회_성공() {
        // given
        ottRepository.save(testOttBuilder(1L,"Netflix", 4, 1000L, "netflix.image"));
        ottRepository.save(testOttBuilder(2L,"Watcha", 4, 2000L, "watcha.image"));
        ottRepository.save(testOttBuilder(3L, "Disney Plus", 4, 3000L, "disney.plus.image"));

        // when
        List<Ott> findOttList = ottRepository.findAll();

        // then
        assertThat(findOttList.size()).isEqualTo(3);
        assertThat(findOttList.get(0).getOttName()).isEqualTo("Netflix");
        assertThat(findOttList.get(1).getOttName()).isEqualTo("Watcha");
        assertThat(findOttList.get(2).getOttName()).isEqualTo("Disney Plus");
    }

    @Test
    @DisplayName("Ott 레포지토리 : Ott 플랫폼 조회 실패")
    void OTT_플랫폼_전체_조회_실패() {
        // when
        List<Ott> findOttList = ottRepository.findAll();

        // then
        assertThat(findOttList).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Ott 레포지토리 : Ott 플랫폼 서비스 저장_성공")
    void OTT_플랫폼_서비스_저장_성공() {
        //w when
        ottRepository.save(testOttBuilder(1L, "Netflix", 4, 1000L, "test"));
        Ott findOtt = ottRepository.findByOttId(1L);

        // then
        assertThat(findOtt.getOttName()).isEqualTo("Netflix");
        assertThat(findOtt.getOttHeadcount()).isEqualTo(4);
        assertThat(findOtt.getOttPrice()).isEqualTo(1000L);
        assertThat(findOtt.getOttImage()).isEqualTo("test");
    }

    @Test
    @DisplayName("Ott 레포지토리 : Ott 플랫폼 서비스 한개 조회 성공")
    void OTT_플랫폼_서비스_한개_조회_성공() {
         // given
        ottRepository.save(testOttBuilder(1L, "Netflix", 4, 1000L, "test"));

        // when
        Ott findOtt = ottRepository.findByOttId(1L);

        // then
        assertThat(findOtt.getOttName()).isEqualTo("Netflix");
        assertThat(findOtt.getOttHeadcount()).isEqualTo(4);
        assertThat(findOtt.getOttPrice()).isEqualTo(1000L);
        assertThat(findOtt.getOttImage()).isEqualTo("test");
    }

    @Test
    @DisplayName("Ott 레포지토리 : Ott 플랫폼 서비스 삭제 성공")
    void OTT_플랫폼_서비스_삭제_성공() {
        // given
        ottRepository.save(testOttBuilder(1L, "Netflix", 4, 1000L, "test"));

        // when
        ottRepository.deleteByOttId(1L);
        Ott findOtt = ottRepository.findByOttId(1L);

        // then
        assertThat(findOtt).isNull();
    }

    private static Ott testOttBuilder(Long ottId, String ottName, Integer ottHeadcount, Long ottPrice, String ottImage) {
        return Ott.builder()
                .ottId(ottId)
                .ottName(ottName)
                .ottHeadcount(ottHeadcount)
                .ottPrice(ottPrice)
                .ottImage(ottImage).build();
    }
}