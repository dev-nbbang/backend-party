package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.DuplicateOttAccException;
import com.dev.nbbang.party.domain.party.exception.NoCreatePartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
    @Mock
    private PartyRepository partyRepository;

    @Mock
    private QnaRepository qnaRepository;

    @InjectMocks
    private PartyServiceImpl partyService;

    @Test
    @DisplayName("파티 서비스 : 파티 생성 성공")
    void 파티_생성_성공() {
        // given
        given(partyRepository.save(any())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO savedParty = partyService.createParty(testPartyBuilder(1L, "zayson"));

        // then
        assertThat(savedParty.getPartyId()).isEqualTo(1L);
        assertThat(savedParty.getLeaderId()).isEqualTo("leader");
        assertThat(savedParty.getOttAccId()).isEqualTo("zayson");
        assertThat(savedParty.getOtt().getOttId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("파티 서비스 : 파티 생성 실패")
    void 파티_생성_실패() {
        // given
        given(partyRepository.save(any())).willThrow(NoCreatePartyException.class);

        // then
        assertThrows(NoCreatePartyException.class, () -> partyService.createParty(testPartyBuilder(1L, "zayson")));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 상세 정보 조회 성공")
    void 파티_상세_정보_조회_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO findParty = partyService.findPartyByPartyId(1L);

        // then
        assertThat(findParty.getPartyId()).isEqualTo(1L);
        assertThat(findParty.getLeaderId()).isEqualTo("leader");
        assertThat(findParty.getOttAccId()).isEqualTo("zayson");
        assertThat(findParty.getOtt().getOttId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("파티 서비스 : 파티 상세 정보 조회 실패")
    void 파티_상세_정보_조회_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> partyService.findPartyByPartyId(1L));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 해체 성공")
    void 파티_해체_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        partyService.deleteParty(1L,"leader");

        // then
        verify(qnaRepository, times(1)).deleteByParty(testPartyBuilder(1L, "zayson"));
        verify(partyRepository, times(1)).deleteByPartyId(1L);

    }

    @Test
    @DisplayName("파티 서비스 : 파티 해체 실패")
    void 파티_해체_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> partyService.deleteParty(1L, "ledaer"));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 정보 수정 성공")
    void 파티_정보_수정_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO updatedParty = partyService.updatePartyInformation(1L, "update title", "update partyDetail", "leader");

        // then
        assertThat(updatedParty.getPartyId()).isEqualTo(1L);
        assertThat(updatedParty.getTitle()).isEqualTo("update title");
        assertThat(updatedParty.getPartyDetail()).isEqualTo("update partyDetail");
        assertThat(updatedParty.getLeaderId()).isEqualTo("leader");
    }

    @Test
    @DisplayName("파티 서비스 : 파티 정보 수정 실패")
    void 파티_정보_수정_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> partyService.updatePartyInformation(1L, "update title", "update partyDetail", "leader"));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 리스트 조회(필터링x) 성공 ")
    void 파티_리스트_조회_필터링X_성공() {
        // given
        given(partyRepository.findPartyList(any(), anyInt(), anyLong(), any())).willReturn(testPartyListBuilder());

        // when
        List<PartyDTO> findPartyList = partyService.findPartyList(testOttBuilder(), 1L, 3);

        // then
        assertThat(findPartyList.size()).isEqualTo(3);
        for (PartyDTO party : findPartyList) {
            assertThat(party.getOtt().getOttId()).isEqualTo(1L);
            assertThat(party.getPresentHeadcount()).isLessThan(party.getMaxHeadcount());
        }
    }

    @Test
    @DisplayName("파티 서비스 : 파티 리스트 조회(필터링X) 실패 ")
    void 파티_리스트_조회_필터링X_실패() {
        // given
        given(partyRepository.findPartyList(any(), anyInt(), anyLong(), any())).willThrow(NoSuchPartyException.class);

        // when
        assertThrows(NoSuchPartyException.class, () -> partyService.findPartyList(testOttBuilder(), 1L, 3));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 리스트 조회(필터링 O) 성공 ")
    void 파티_리스트_조회_필터링O_성공() {
        // given
        given(partyRepository.findPartyList(anyInt(), any(), anyInt(), anyLong(), any())).willReturn(testPartyListBuilder());

        // when
        List<PartyDTO> findPartyList = partyService.findPartyListByMatchingType(1,testOttBuilder(), 1L, 3);

        // then
        assertThat(findPartyList.size()).isEqualTo(3);
        for (PartyDTO party : findPartyList) {
            assertThat(party.getOtt().getOttId()).isEqualTo(1L);
            assertThat(party.getPresentHeadcount()).isLessThan(party.getMaxHeadcount());
        }
    }

    @Test
    @DisplayName("파티 서비스 : 파티 리스트 조회(필터링 O) 실패 ")
    void 파티_리스트_조회_필터링O_실패() {
        // given
        given(partyRepository.findPartyList(anyInt(), any(), anyInt(), anyLong(), any())).willThrow(NoSuchPartyException.class);

        // when
        assertThrows(NoSuchPartyException.class, () -> partyService.findPartyListByMatchingType(1, testOttBuilder(), 1L, 3));
    }

    @Test
    @DisplayName("파티 서비스 : OTT 계정 중복 확인 성공 ")
    void OTT_계정_중복확인_성공() {
        // given
        given(partyRepository.findByOttAndOttAccId(any(), anyString())).willReturn(null);

        // when
        Boolean validOttAcc = partyService.duplicateOttAcc(testOttBuilder(), "test");

        // then
        assertThat(validOttAcc).isTrue();
    }

    @Test
    @DisplayName("파티 서비스 : OTT 계정 중복확인 성공 실패 ")
    void OTT_계정_중복확인_실패() {
        // given
        given(partyRepository.findByOttAndOttAccId(any(), anyString())).willThrow(DuplicateOttAccException.class);

        // then
        assertThrows(DuplicateOttAccException.class, () -> partyService.duplicateOttAcc(testOttBuilder(), "zayson"));
    }

    @Test
    @DisplayName("파티 서비스 : 파티 공지 수정 성공 ")
    void 파티_공지_수정_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO updatedParty = partyService.updatePartyNotice(NoticeType.NEW, 1L, "leader", "partyNotice");

        // then
        assertThat(updatedParty.getPartyNotice()).isEqualTo("partyNotice");
    }

    @Test
    @DisplayName("파티 서비스 : 파티 공지 수정 실패 ")
    void 파티_공지_수정실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // when
        assertThrows(NoSuchPartyException.class, () -> partyService.updatePartyNotice(NoticeType.MODIFY, 1L, "leader", "partyNotice"));
    }

    @Test
    @DisplayName("파티 서비스 : OTT ID/PW 조회 성공")
    void OTT_ID_PW_조회_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO findParty = partyService.findPartyByPartyId(1L);

        // then
        assertThat(findParty.getOttAccId()).isEqualTo("zayson");
        assertThat(findParty.getOttAccPw()).isEqualTo("1234");
    }

    @Test
    @DisplayName("파티 서비스 : OTT ID/PW 조회 실패")
    void OTT_ID_PW_조회_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> partyService.findPartyByPartyId(1L));
    }

    @Test
    @DisplayName("파티 서비스 : OTT ID/PW 수정 성공")
    void OTT_ID_PW_수정_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        PartyDTO updateParty = partyService.updateOttAcc(1L, "leader", "update ott id", "update ott pw");

        // then
        assertThat(updateParty.getOttAccId()).isEqualTo("update ott id");
        assertThat(updateParty.getOttAccPw()).isEqualTo("update ott pw");
    }

    @Test
    @DisplayName("파티 서비스 : OTT ID/PW 수정 실패")
    void OTT_ID_PW_수정_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> partyService.updateOttAcc(1L, "leader", "update ott id", "update ott pw"));
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

    private static Party testPartyBuilder(Long ottId, String ottAccId) {
        return Party.builder()
                .partyId(ottId)
                .ott(testOttBuilder())
                .leaderId("leader")
                .presentHeadcount(1)
                .maxHeadcount(4)
                .regYmd(LocalDateTime.now())
                .ottAccId(ottAccId)
                .ottAccPw("1234")
                .matchingType(1)
                .title("title")
                .price(3000L)
                .period(30).build();
    }

    private static Slice<Party> testPartyListBuilder() {
        Party first = testPartyBuilder(1L, "Maeng");
        Party second = testPartyBuilder(2L, "Joon");
        Party third = testPartyBuilder(3L, "Young");

        return new SliceImpl<>(Arrays.asList(first,second,third));
    }
}