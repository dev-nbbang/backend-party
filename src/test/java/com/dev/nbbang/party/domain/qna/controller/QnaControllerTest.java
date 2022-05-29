package com.dev.nbbang.party.domain.qna.controller;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.dto.request.AnswerRequest;
import com.dev.nbbang.party.domain.qna.dto.request.QuestionCreateRequest;
import com.dev.nbbang.party.domain.qna.dto.request.QuestionModifyRequest;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoCreateQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import com.dev.nbbang.party.domain.qna.service.QnaService;
import com.dev.nbbang.party.global.config.WebConfig;
import com.dev.nbbang.party.global.exception.NbbangExceptionHandler;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QnaController.class)
class QnaControllerTest {
    @MockBean
    private QnaService qnaService;

    @MockBean
    private PartyService partyService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mvc = MockMvcBuilders.standaloneSetup(new QnaController(this.qnaService, this.partyService)).setControllerAdvice(NbbangExceptionHandler.class).build();
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 생성 성공")
    void 파티_문의_생성_성공() throws Exception {
        // given
        String uri = "/qna/new";
        given(partyService.findPartyByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaService.createQuestion(any())).willReturn(testQnaBuilder(1L));

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .header("X-Authorization-Id", "sender")
                .content(objectMapper.writeValueAsString(testQuestionCreateRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.qnaId").value(1))
                .andExpect(jsonPath("$.data.partyId").value(1))
                .andExpect(jsonPath("$.data.qnaSender").value("sender"))
                .andExpect(jsonPath("$.data.qnaStatus").value("Q"))
                .andExpect(jsonPath("$.data.questionDetail").value("질문 내용"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 생성 실패")
    void 파티_문의_생성_실패() throws Exception {
        // given
        String uri = "/qna/new";
        given(partyService.findPartyByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaService.createQuestion(any())).willThrow(new NoCreateQnaException("파티 문의 생성 실패", NbbangException.NO_CREATE_QUESTION));

        //when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .header("X-Authorization-Id", "sender")
                .content(objectMapper.writeValueAsString(testQuestionCreateRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의/답변 조회 성공")
    void 파티_문의_답변_조회_성공() throws Exception {
        // given
        String uri = "/qna/1";
        given(qnaService.findAllQnA(anyLong(), anyString())).willReturn(testQnaListBuilder());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "sender"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].qnaSender").value("sender"))
                .andExpect(jsonPath("$.data.[1].qnaSender").value("sender"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의/답변 조회 실패")
    void 파티_문의_답변_조회_실패() throws Exception {
        // given
        String uri = "/qna/1";
        given(qnaService.findAllQnA(anyLong(), anyString())).willThrow(new NoSuchQnaException("문의내역 없음",NbbangException.NOT_FOUND_QNA));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "sender"))
                .andExpect(jsonPath("$.status").value(false))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 삭제 성공")
    void 파티_문의_삭제_성공() throws Exception {
        // given
        String uri = "/qna/1";
        //        doNothing().when(qnaService).deleteQuestion(anyLong());
        given(qnaService.deleteQuestion(anyLong())).willReturn(true);

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "sender"))
                .andExpect(status().isNoContent())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 삭제 실패")
    void 파티_문의_삭제_실패() throws Exception {
        // given
        String uri = "/qna/1";
        given(qnaService.deleteQuestion(anyLong())).willThrow(new FailDeleteQnaException("삭제 실패", NbbangException.FAIL_TO_DELETE_QNA));

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri)
                .header("X-Authorization-Id", "sender"))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 수정 성공")
    void 파티_문의_수정_성공() throws Exception {
        // given
        String uri = "/qna/1";
        String questionDetail = "수정 내용";
        given(qnaService.modifyQuestion(anyLong(), anyString())).willReturn(testModifyQnaBuilder(questionDetail));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "sender")
                .content(objectMapper.writeValueAsString(testQuestionModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.questionDetail").value(questionDetail))
                .andExpect(jsonPath("$.data.qnaStatus").value("Q"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 문의 수정 실패")
    void 파티_문의_수정_실패() throws Exception {
        // given
        String uri = "/qna/1";
        given(qnaService.modifyQuestion(anyLong(), anyString())).willThrow(new NoSuchQnaException("문의내역 없음",NbbangException.NOT_FOUND_QNA));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "sender")
                .content(objectMapper.writeValueAsString(testQuestionModifyRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 답변 관리 성공")
    void 파티_답변_관리_성공() throws Exception {
        // given
        String uri = "/qna/1/answer/new";
        String answerDetail = "답변 내용";
        given(qnaService.manageAnswer(anyLong(), anyString(), any())).willReturn(testAnswerQnaBuilder(answerDetail));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-id", "leader")
                .content(objectMapper.writeValueAsString(testAnswerRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.qnaId").value(1))
                .andExpect(jsonPath("$.data.partyId").value(1))
                .andExpect(jsonPath("$.data.qnaSender").value("sender"))
                .andExpect(jsonPath("$.data.qnaStatus").value("A"))
                .andExpect(jsonPath("$.data.questionDetail").value("질문 내용"))
                .andExpect(jsonPath("$.data.answerDetail").value("답변 내용"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 파티 답변 관리 실패")
    void 파티_답변_관리_실패() throws Exception {
        // given
        String uri = "/qna/1/answer/delete";
        given(qnaService.manageAnswer(anyLong(), anyString(), any())).willThrow(new NoSuchQnaException("문의내역 없음",NbbangException.NOT_FOUND_QNA));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .header("X-Authorization-Id", "sender")
                .content(objectMapper.writeValueAsString(testAnswerRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 미답변 문의 내역 리스트 조회 성공")
    void 미답변_문의내역_리스트_조회_성공() throws Exception {
        // given
        String uri = "/qna/1/unanswer/list";
        given(qnaService.findAllUnansweredQuestion(anyLong())).willReturn(testQnaListBuilder());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].qnaStatus").value("Q"))
                .andExpect(jsonPath("$.data.[1].qnaStatus").value("Q"))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Qna 컨트롤러 : 미답변 문의 내역 리스트 조회 실패")
    void 미답변_문의내역_리스트_조회_실패() throws Exception {
        // given
        String uri = "/qna/1/unanswer/list";
        given(qnaService.findAllUnansweredQuestion(anyLong())).willThrow(new NoSuchQnaException("문의내역 없음",NbbangException.NOT_FOUND_QNA));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("X-Authorization-Id", "leader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static QnaDTO testQnaBuilder(Long qnaId) {
        return QnaDTO.builder()
                .qnaId(qnaId)
                .party(PartyDTO.toEntity(testPartyBuilder(1L)))
                .qnaYmd(LocalDateTime.now())
                .qnaSender("sender")
                .qnaStatus(QnaStatus.Q)
                .questionDetail("질문 내용")
                .build();
    }

    private static QnaDTO testModifyQnaBuilder(String questionDetail) {
        return QnaDTO.builder()
                .qnaId(1L)
                .party(PartyDTO.toEntity(testPartyBuilder(1L)))
                .qnaYmd(LocalDateTime.now())
                .qnaSender("sender")
                .qnaStatus(QnaStatus.Q)
                .questionDetail(questionDetail)
                .build();
    }

    private static QnaDTO testAnswerQnaBuilder(String answerDetail) {
        return QnaDTO.builder()
                .qnaId(1L)
                .party(PartyDTO.toEntity(testPartyBuilder(1L)))
                .qnaYmd(LocalDateTime.now())
                .qnaSender("sender")
                .qnaStatus(QnaStatus.A)
                .questionDetail("질문 내용")
                .answerDetail(answerDetail)
                .build();
    }

    private static List<QnaDTO> testQnaListBuilder() {
        List<QnaDTO> qnaList = new ArrayList<>();
        qnaList.add(testQnaBuilder(1L));
        qnaList.add(testQnaBuilder(2L));

        return qnaList;
    }

    private static PartyDTO testPartyBuilder(Long partyId) {
        return PartyDTO.builder()
                .partyId(partyId)
                .leaderId("leader")
                .build();
    }

    private static QuestionCreateRequest testQuestionCreateRequest() {
        return QuestionCreateRequest.builder()
                .partyId(1L)
                .qnaSender("sender")
                .questionDetail("질문 내용")
                .build();
    }

    public static QuestionModifyRequest testQuestionModifyRequest() {
        return QuestionModifyRequest.builder()
                .questionDetail("수정 내용")
                .build();
    }

    public static AnswerRequest testAnswerRequest() {
        return AnswerRequest.builder()
                .answerDetail("답변 내용")
                .build();
    }

}