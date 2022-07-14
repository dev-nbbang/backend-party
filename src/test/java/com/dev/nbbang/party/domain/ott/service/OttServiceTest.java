package com.dev.nbbang.party.domain.ott.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.exception.FailDeleteOttException;
import com.dev.nbbang.party.domain.ott.exception.NoCreateOttException;
import com.dev.nbbang.party.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OttServiceTest {
    @Mock
    private OttRepository ottRepository;

    @InjectMocks
    private OttServiceImpl ottService;

    @Test
    @DisplayName("ott 서비스 : ottId 로 Ott 서비스 조회 성공")
    void ott_서비스_조회_성공() {
        // given
        given(ottRepository.findByOttId(anyLong())).willReturn(testOttBuilder(1L, "Netflix"));

        // when
        OttDTO findOtt = ottService.findOtt(1L);

        // then
        assertThat(findOtt.getOttName()).isEqualTo("Netflix");
        assertThat(findOtt.getOttHeadcount()).isEqualTo(4);
        assertThat(findOtt.getOttPrice()).isEqualTo(1000L);
        assertThat(findOtt.getOttImage()).isEqualTo("test");
    }

    @Test
    @DisplayName("ott 서비스 : ottId 로 Ott 서비스 조회 실패")
    void ott_서비스_조회_실패() {
        // given
        given(ottRepository.findByOttId(anyLong())).willThrow(NoSuchOttException.class);

        // then
        assertThrows(NoSuchOttException.class, () -> ottService.findOtt(1L));
    }

    @Test
    @DisplayName("ott 서비스 : Ott 서비스 전체 조회 성공")
    void ott_서비스_전체_조회_성공() {
        // given
        given(ottRepository.findAll()).willReturn(testOttListBuilder());

        // when
        List<OttDTO> findOttList = ottService.findAllOtt();

        // then
        assertThat(findOttList.size()).isEqualTo(3);
        assertThat(findOttList.get(0).getOttName()).isEqualTo("Netflix");
        assertThat(findOttList.get(1).getOttName()).isEqualTo("Watcha");
        assertThat(findOttList.get(2).getOttName()).isEqualTo("Disney Plus");
    }

    @Test
    @DisplayName("ott 서비스 : Ott 서비스 전체 조회 실패")
    void ott_서비스_전체_조회_실패() {
        // given
        given(ottRepository.findAll()).willReturn(Collections.emptyList());

        // then
        assertThrows(NoSuchOttException.class, () -> ottService.findAllOtt());
    }

    @Test
    @DisplayName("ott 서비스 : Ott 서비스 수정 성공")
    void ott_서비스_수정_성공() {
        // given
        given(ottRepository.findByOttId(anyLong())).willReturn(testOttBuilder(1L, "Netflix"));

        // when
        OttDTO updatedOtt = ottService.modifyOtt(1L, testOttBuilder(1L, "Modify"));

        // then
        assertThat(updatedOtt.getOttName()).isEqualTo("Modify");
        assertThat(updatedOtt.getOttHeadcount()).isEqualTo(4);
        assertThat(updatedOtt.getOttPrice()).isEqualTo(1000L);
        assertThat(updatedOtt.getOttImage()).isEqualTo("test");
    }

    @Test
    @DisplayName("ott 서비스 : ott 서비스 수정 실패")
    void ott_서비스_수정_실패() {
        // given
        given(ottRepository.findByOttId(anyLong())).willThrow(NoSuchOttException.class);

        // when
        assertThrows(NoSuchOttException.class, () -> ottService.modifyOtt(1L, testOttBuilder(1L, "Modify")));
    }

    @Test
    @DisplayName("ott 서비스 : ott 서비스 저장 성공")
    void ott_서비스_저장_성공() {
        // given
        given(ottRepository.save(any())).willReturn(testOttBuilder(1L, "Netflix"));

        // when
        OttDTO savedOtt = ottService.saveOtt(testOttBuilder(1L, "Netflix"));

        // then
        assertThat(savedOtt.getOttName()).isEqualTo("Netflix");
        assertThat(savedOtt.getOttImage()).isEqualTo("test");
        assertThat(savedOtt.getOttHeadcount()).isEqualTo(4);
        assertThat(savedOtt.getOttPrice()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("ott 서비스 : ott 서비스 저장 실패")
    void ott_서비스_저장_실패() {
        // given
        given(ottRepository.save(any())).willThrow(NoCreateOttException.class);

        // then
        assertThrows(NoCreateOttException.class, () -> ottService.saveOtt(testOttBuilder(1L, "Netflix")));
    }

    @Test
    @DisplayName("ott 서비스 : Ott 서비스 삭제 성공")
    void ott_서비스_삭제_성공() {
        // given
        given(ottRepository.findByOttId(anyLong())).willReturn(null);

        ottService.deleteOtt(1L);

        // when
        verify(ottRepository,times(1)).deleteByOttId(1L);

    }

    @Test
    @DisplayName("ott 서비스 : ott 서비스 삭제 실패")
    void ott_서비스_삭제_실패() {
        // given
        doNothing().when(ottRepository).deleteByOttId(anyLong());
        given(ottRepository.findByOttId(anyLong())).willThrow(FailDeleteOttException.class);

        // then
        assertThrows(FailDeleteOttException.class, () -> ottService.deleteOtt(1L));
    }
    private static Ott testOttBuilder(Long ottId, String ottName) {
        return Ott.builder()
                .ottId(ottId)
                .ottName(ottName)
                .ottHeadcount(4)
                .ottPrice(1000L)
                .ottImage("test")
                .build();
    }

    private static List<Ott> testOttListBuilder() {
        List<Ott> ottList = new ArrayList<>();
        ottList.add(testOttBuilder(1L, "Netflix"));
        ottList.add(testOttBuilder(1L, "Watcha"));
        ottList.add(testOttBuilder(1L, "Disney Plus"));

        return ottList;
    }
}