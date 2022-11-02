package com.dev.nbbang.party.domain.party.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.*;
import com.dev.nbbang.party.domain.party.dto.response.ParticipantPartyResponse;
import com.dev.nbbang.party.domain.party.dto.response.ParticipantValidResponse;
import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.*;
import com.dev.nbbang.party.domain.party.service.ParticipantService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.TransactionScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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

    @MockBean
    private ParticipantService participantService;

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
    void 파티_조회_성공() throws Exception {
        // given
        String uri = "/party/1";
        given(partyService.findPartyByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.partyId").value(1))
                .andExpect(jsonPath("$.data.ott.ottId").value(1))
                .andExpect(jsonPath("$.data.ott.ottName").value("test"))
                .andExpect(jsonPath("$.data.leaderId").value("leader"))
                .andExpect(jsonPath("$.data.ottAccId").value("zayson"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
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
    void 파티_해체_성공() throws Exception {
        // given
        String uri = "/party/1";
        doNothing().when(partyService).deleteParty(anyLong(), anyString());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "leader"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn().getResponse();
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
    void 일반결제_파티_정보_수정_성공() throws Exception {
        // given
        String uri = "/party/1";
        given(partyService.updatePartyInformation(anyLong(), anyString(), anyString(), anyString())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testPartyModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.partyDetail").value("partyDetail"))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
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
    void 파티_리스트_조회_성공_필터링X() throws Exception {
        // given
        String uri = "/party/1/list/all";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.findPartyList(any(), anyLong(), anyInt())).willReturn(testPartyListBuilder());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader")
                .param("partyId", "10000")
                .param("size", "2"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].ott.ottId").value(1))
                .andExpect(jsonPath("$.data.[1].ott.ottId").value(1))
                .andExpect(jsonPath("$.data.[0].partyId").value(1))
                .andExpect(jsonPath("$.data.[1].partyId").value(2))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

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
    void 파티_리스트_조회_성공_필터링O() throws Exception {
        // given
        String uri = "/party/1/list/1";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.findPartyListByMatchingType(anyInt(), any(), anyLong(), anyInt())).willReturn(testPartyListBuilder());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader")
                .param("partyId", "1000")
                .param("size", "2"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].ott.ottId").value(1))
                .andExpect(jsonPath("$.data.[1].ott.ottId").value(1))
                .andExpect(jsonPath("$.data.[0].partyId").value(1))
                .andExpect(jsonPath("$.data.[1].partyId").value(2))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
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
    void OTT_계정_중복_등록_검증_성공() throws Exception {
        // given
        String uri = "/party/ott-acc/validation";
        given(ottService.findOtt(anyLong())).willReturn(testOtt());
        given(partyService.duplicateOttAcc(any(), anyString())).willReturn(Boolean.TRUE);

        // then
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testOttAccRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.validOttAcc").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
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
    void 파티_공지_관리_성공() throws Exception {
        // given
        String uri = "/party/1/notice/modify";
        given(partyService.updatePartyNotice(any(), anyLong(), anyString(), anyString())).willReturn(testPartyBuilder(1L, "zayson"));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testNoticeRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.partyId").value(1))
                .andExpect(jsonPath("$.data.partyNotice").value("partyNotice"))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
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
    void OTT_계정_조회_성공() throws Exception {
        // given
        String uri = "/party/1/ott-acc";
        given(partyService.findPartyByPartyId(anyLong())).willReturn(testPartyBuilder(1L, "zayson"));

        // then
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottAccId").value("zayson"))
                .andExpect(jsonPath("$.data.ottAccPw").value("1234"))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
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
    void OTT_계정_수정_성공() throws Exception {
        // given
        String uri = "/party/1/ott-acc";
        given(partyService.updateOttAcc(anyLong(), anyString(), anyString(), anyString()))
                .willReturn(testPartyBuilder(1L, "update Id"));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "leader")
                .content(objectMapper.writeValueAsString(testOttAccInformation()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottAccId").value("update Id"))
                .andExpect(jsonPath("$.data.ottAccPw").value("1234"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
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

    @Test
    @DisplayName("파티 컨트롤러 : 파티원이 파티 탈퇴 성공")
    void 파티원이_자의로_파티탈퇴_성공() throws Exception {
        // given
        String uri = "/party/1/participant";
        doNothing().when(participantService).outFromParty(anyLong(), anyString());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "participant"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티원이 파티 탈퇴 실패")
    void 파티원이_파티_탈퇴_실패() throws Exception {
        // given
        String uri = "/party/1/participant";
        doThrow(new NoSuchParticipantException("탈퇴 실패", NbbangException.NOT_FOUND_PARTICIPANT)).when(participantService).outFromParty(anyLong(), anyString());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "participant"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 가입 여부 조회 성공")
    void 파티_가입_여부_조회_성공() throws Exception {
        // given
        String uri = "/party/1/nickname";
        given(participantService.validParticipateParty(anyLong(), anyString())).willReturn(Boolean.TRUE);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "particpant")
                .content(objectMapper.writeValueAsString(testParticipantValidRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.validJoinParty").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 파티 가입 여부 조회 실패")
    void 파티_가입_여부_조회_실패() throws Exception {
        // given
        String uri = "/party/1/nickname";
        given(participantService.validParticipateParty(anyLong(), anyString())).willThrow(new AlreadyJoinPartyException("파티 가입", NbbangException.ALREADY_JOIN_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "particpant")
                .content(objectMapper.writeValueAsString(testParticipantValidRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 일주일 매칭 인원수 조회 성공")
    void 일주일_매칭_인원수_조회_성공() throws Exception {
        // given
        String uri = "/party/1/matching/week";
        given(participantService.matchingCountForWeek(anyLong())).willReturn(3);

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "participant"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.matchingCount").value(3))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("파티 컨트롤러 : 내 파티 조회 성공")
    void 내_파티_조회_성공() throws Exception {
        // given
        String uri = "/party/my-party";
        given(participantService.findMyParty(anyString())).willReturn(testParticipantListByOne());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "zayson"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].leaderId").value("leader"))
                .andExpect(jsonPath("$.data.[0].matchingType").value(1))
                .andExpect(jsonPath("$.data.[0].maxHeadcount").value(5))
                .andExpect(jsonPath("$.data.[0].ott.ottId").value(1))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("퍄티 컨트롤러 : 내 파티 조회 실패")
    void 내_파티_조회_실패() throws Exception {
        // given
        String uri = "/party/my-party";
        given(participantService.findMyParty(anyString())).willThrow(new NoJoinPartyException("error", NbbangException.NO_JOIN_PARTY));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "zayson"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    public static List<ParticipantPartyResponse> testParticipantListByOne() {
        Party party1 = makeParty("leader",1, 5,1L,1L,30, 2, 1000L);
        Party party2 = makeParty("leader",1, 5,2L,2L,30, 2, 1000L);
        Party party3 = makeParty("leader",1, 5,3L,3L,30, 2, 1000L);

        return new ArrayList<>(List.of(
                ParticipantPartyResponse.create(party1),
                ParticipantPartyResponse.create(party2),
                ParticipantPartyResponse.create(party3)
        ));
    }

    private static Party makeParty(String leaderId, Integer matchingType, Integer maxHeadcount, Long ottId, Long partyId, Integer period, Integer presentHeadcount, Long price) {
        return Party.builder().leaderId(leaderId)
                .matchingType(matchingType)
                .maxHeadcount(maxHeadcount)
                .ott(Ott.builder().ottId(ottId).build())
                .partyId(partyId)
                .period(period)
                .presentHeadcount(presentHeadcount)
                .price(price)
                .build();
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
                .partyNotice("partyNotice")
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

    private static ParticipantValidRequest testParticipantValidRequest() {
        return ParticipantValidRequest.builder()
                .participantId("participant")
                .build();
    }
}