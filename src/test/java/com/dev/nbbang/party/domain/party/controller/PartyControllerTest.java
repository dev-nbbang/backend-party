package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.*;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.DuplicateOttAccException;
import com.dev.nbbang.party.domain.party.exception.FailDeletePartyException;
import com.dev.nbbang.party.domain.party.exception.NoCreatePartyException;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.party.service.PartyServiceImpl;
import com.dev.nbbang.party.global.config.WebConfig;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PartyController.class)
class PartyControllerTest {
    @MockBean
    private PartyService partyService;

    @MockBean
    private OttService ottService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("파티 컨트롤러 : 파티 생성 성공")
    void 파티_생성_성공() throws Exception {
        // given
        String uri = "/party/new";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.createParty(any())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testPartyCreateRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.partyId").value(1))
                .andExpect(jsonPath("$.data.ott.ottId").value(1))
                .andExpect(jsonPath("$.data.ott.ottName").value("test"))
                .andExpect(jsonPath("$.data.leaderId").value("leader"))
                .andExpect(jsonPath("$.data.presentHeadcount").value(1))
                .andExpect(jsonPath("$.data.maxHeadcount").value(4))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 생성 실패")
    void 파티_생성_실패() throws Exception {
        //given
        String uri = "/party/new";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.createParty(any())).willThrow(new NoCreatePartyException("생성 실패", NbbangException.NO_CREATE_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testPartyCreateRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("생성 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 조회 성공")
    void 파티_조회_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 조회 실패")
    void 파티_조회_실패() throws Exception {
        //given
        String uri = "/party/1";
        given(partyService.findPartyByPartyId(anyLong())).willThrow(new NoSuchPartyException("조회 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("조회 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 해체 성공")
    void 파티_해체_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 해체 실패")
    void 파티_해체_실패() throws Exception {
        //given
        String uri = "/party/1";
        doThrow(new FailDeletePartyException("해체 실패", NbbangException.FAIL_TO_DELETE_PARTY)).when(partyService).deleteParty(anyLong(), anyString());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "leader"))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("해체 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 일반 결제 파티 정보 수정 성공")
    void 일반결제_파티_정보_수정_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 일반 결제 파티 정부 수정 실패")
    void 일반결제_파티_정보_수정_실패() throws Exception {
        //given
        String uri = "/party/1";
        given(partyService.updatePartyInformation(anyLong(), anyString(), anyString(), anyString()))
                .willThrow(new NoSuchPartyException("수정 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testPartyModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("수정 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 리스트 조회 성공(필터링 X)")
    void 파티_리스트_조회_성공_필터링X() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 리스트 조회 실패(필터링 X)")
    void 파티_리스트_조회_실패_필터링X() throws Exception {
        //given
        String uri = "/party/1/list/all";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.findPartyList(any(), anyLong(), anyInt()))
                .willThrow(new NoSuchPartyException("리스트 조회 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader")
                .param("partyId", "10000")
                .param("size", "2"))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("리스트 조회 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 리스트 조회 성공(필터링 O)")
    void 파티_리스트_조회_성공_필터링O() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 리스트 조회 실패(필터링 O)")
    void 파티_리스트_조회_실패_필터링O() throws Exception {
        //given
        String uri = "/party/1/list/1";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.findPartyListByMatchingType(anyInt(), any(), anyLong(), anyInt()))
                .willThrow(new NoSuchPartyException("리스트 조회 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader")
                .param("partyId", "1000")
                .param("size", "2"))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("리스트 조회 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 중복 등록 검증 성공")
    void OTT_계정_중복_등록_검증_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 중복 등록 검증 실패")
    void OTT_계정_중복_등록_검증_실패() throws Exception {
        //given
        String uri = "/party/ott-acc/validation";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.duplicateOttAcc(any(), anyString())).willThrow(new DuplicateOttAccException("중복 계정", NbbangException.DUPLICATE_OTT_ACC));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testOttAccRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("중복 계정"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 공지 관리 성공")
    void 파티_공지_관리_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 공지 관리 실패")
    void 파티_공지_관리_실패() throws Exception {
        //given
        String uri = "/party/1/notice/new";
        given(partyService.updatePartyNotice(any(), anyLong(), anyString(), anyString()))
                .willThrow(new NoSuchPartyException("관리 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testNoticeRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("관리 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 조회 성공")
    void OTT_계정_조회_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 조회 실패")
    void OTT_계정_조회_실패() throws Exception {
        //given
        String uri = "/party/1/ott-acc";
        given(partyService.findPartyByPartyId(anyLong()))
                .willThrow(new NoSuchPartyException("조회 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader"))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("조회 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 수정 성공")
    void OTT_계정_수정_성공() {

    }

    @Test
    @DisplayName("파티 컨트롤러 : OTT 계정 수정 실패")
    void OTT_계정_수정_실패() throws Exception {
        //given
        String uri = "/party/1/ott-acc";
        given(partyService.updateOttAcc(anyLong(), anyString(), anyString(), anyString()))
                .willThrow(new NoSuchPartyException("수정 실패", NbbangException.NOT_FOUND_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testOttAccInformation()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("수정 실패"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
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

    private static OttDTO testOtt() {
        return OttDTO.builder()
                .ottId(1L)
                .ottName("test")
                .ottHeadcount(4)
                .ottPrice(3000L)
                .ottImage("test.image")
                .build();
    }

    private static PartyDTO testPartyBuilder(Long ottId, String ottAccId) {
        return PartyDTO.builder()
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
                .partyDetail("partyDetail")
                .price(3000L)
                .period(30).build();
    }

    private static List<PartyDTO> testPartyListBuilder() {
        PartyDTO first = testPartyBuilder(1L, "Maeng");
        PartyDTO second = testPartyBuilder(2L, "Joon");
        PartyDTO third = testPartyBuilder(3L, "Young");

        return new ArrayList<>(Arrays.asList(first, second, third));
    }

    private static PartyCreateRequest testPartyCreateRequest() {
        return PartyCreateRequest.builder()
                .ottId(1L)
                .leaderId("leader")
                .maxHeadcount(4)
                .ottAccId("zayson")
                .ottAccPw("1234")
                .matchingType(1)
                .title("title")
                .price(3000L)
                .partyDetail("partyDetail")
                .build();
    }

    private static PartyModifyRequest testPartyModifyRequest() {
        return PartyModifyRequest.builder()
                .title("update title")
                .partyDetail("update partyDetail")
                .build();
    }

    private static PartyOttAccRequest testOttAccRequest() {
        return PartyOttAccRequest.builder()
                .ottId(1L)
                .ottAccId("zayson")
                .build();
    }

    private static PartyOttAccInformationRequest testOttAccInformation() {
        return PartyOttAccInformationRequest.builder()
                .ottAccId("update Id")
                .ottAccPw("update Pw")
                .build();
    }

    private static PartyNoticeRequest testNoticeRequest() {
        return PartyNoticeRequest.builder()
                .partyNotice("update Notice")
                .build();
    }
}