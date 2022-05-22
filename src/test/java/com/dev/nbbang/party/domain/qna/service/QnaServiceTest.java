package com.dev.nbbang.party.domain.qna.service;

import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoCreateQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {
    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private PartyRepository partyRepository;

    @InjectMocks
    private QnaServiceImpl qnaService;

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의 내역을 생성하는데 성공한다.")
    void 문의_내역_생성_성공() {
        // given
        given(qnaRepository.save(any())).willReturn(testQnaBuilder(1L));

        // when
        QnaDTO savedQuestion = qnaService.createQuestion(testQnaBuilder(1L));

        // then
        assertThat(savedQuestion.getQnaId()).isEqualTo(1L);
        assertThat(savedQuestion.getQnaSender()).isEqualTo("sender");
        assertThat(savedQuestion.getQnaType()).isEqualTo(QnaType.Q);
    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의 내역 생성에 실패한다.")
    void 문의_내역_생성_실패() {
        // given
        given(qnaRepository.save(any())).willThrow(NoCreateQnaException.class);

        // then
        assertThrows(NoCreateQnaException.class, () -> qnaService.createQuestion(testQnaBuilder(1L)));
    }


    @Test
    @DisplayName("Qna 서비스 : 질문자가 특정 파티에 남긴 모든 문의내역을 조회하는데 성공한다.")
    void 질문자의_모든_문의_내역_조회_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaRepository.findAllByPartyAndQnaSender(any(), anyString())).willReturn(testQnaListBuilder());

        // when
        List<QnaDTO> questionList = qnaService.findAllQnA(1L, "sender");

        // then
        assertThat(questionList.size()).isEqualTo(2);
        for (QnaDTO question : questionList) {
            assertThat(question.getQnaSender()).isEqualTo("sender");
        }

    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 특정 파티에 남긴 모든 문의내역을 조회하는데 실패한다.")
    void 질문자의_모든_문의_내역_조회_실패_파티가_해체된_경우() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willThrow(NoSuchPartyException.class);

        // then
        assertThrows(NoSuchPartyException.class, () -> qnaService.findAllQnA(1L, "sender "));
    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 특정 파티에 남긴 모든 문의내역을 조회하는데 실패한다.")
    void 질문자의_모든_문의_내역_조회_실패_문의내역이_없는_경우() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaRepository.findAllByPartyAndQnaSender(any(), anyString())).willThrow(NoSuchQnaException.class);

        // then
        assertThrows(NoSuchQnaException.class, () -> qnaService.findAllQnA(1L, "sender "));

    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의내역 삭제에 성공한다.")
    void 문의_내역_삭제_성공() {
        // given
        given(qnaRepository.findByQnaId(anyLong())).willReturn(null);

        // when
        boolean result = qnaService.deleteQuestion(1L);

        // then
        assertTrue(result);
        verify(qnaRepository, times(1)).deleteByQnaId(1L);
    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의내역 삭제에 실패한다.")
    void 문의_내역_삭제_실패() {
        // given
        given(qnaRepository.findByQnaId(anyLong())).willThrow(FailDeleteQnaException.class);

        // then
        assertThrows(FailDeleteQnaException.class, () -> qnaService.deleteQuestion(1L));
    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의내역 수정에 성공한다.")
    void 문의_내역_수정_성공() {
        // given
        String questionDetail = "변경 사항";
        given(qnaRepository.findByQnaId(anyLong())).willReturn(testQnaBuilder(1L));

        // when
        QnaDTO updatedQuestion = qnaService.modifyQuestion(1L, questionDetail);

        // then
        assertThat(updatedQuestion.getQuestionDetail()).isEqualTo(questionDetail);
    }

    @Test
    @DisplayName("Qna 서비스 : 질문자가 문의내역 수정에 실패한다.")
    void 문의_내역_수정_실패() {
        // given
        String questionDetail = "변경 사항";
        given(qnaRepository.findByQnaId(anyLong())).willThrow(NoSuchQnaException.class);

        // then
        assertThrows(NoSuchQnaException.class, () -> qnaService.modifyQuestion(1L, questionDetail));
    }

    @Test
    @DisplayName("Qna 서비스 : 파티장이 답변내역 등록 및 수정에 성공한다. answerType =0,1")
    void 답변_등록_및_수정_성공() {
        // given
        Integer answerType = 0;
        String answerDetail = "답변 완료";
        given(qnaRepository.findByQnaId(anyLong())).willReturn(testQnaBuilder(1L));

        // when
        QnaDTO answerQuestion = qnaService.manageAnswer(1L, answerDetail, answerType);

        // then
        assertThat(answerQuestion.getQnaStatus()).isEqualTo(1);
        assertThat(answerQuestion.getQnaType()).isEqualTo(QnaType.A);
        assertThat(answerQuestion.getAnswerDetail()).isEqualTo(answerDetail);
    }

    @Test
    @DisplayName("Qna 서비스 : 파티장이 등록한 답변내역을 삭제하는데 성공한다.")
    void 기존_답변_삭제_성공() {
        // given
        Integer answerType = 2;
        given(qnaRepository.findByQnaId(anyLong())).willReturn(testQnaBuilder(1L));

        // when
        QnaDTO deleteAnswer = qnaService.manageAnswer(1L, "", answerType);

        // then
        assertThat(deleteAnswer.getQnaStatus()).isEqualTo(0);
        assertThat(deleteAnswer.getQnaType()).isEqualTo(QnaType.Q);
        assertThat(deleteAnswer.getAnswerDetail()).isNull();
    }

    @Test
    @DisplayName("Qna 서비스 : 파티장이 답변내역 등록에 실패한다.")
    void 답변_관리_실패() {
        // given
        given(qnaRepository.findByQnaId(anyLong())).willThrow(NoSuchQnaException.class);

        // then
        assertThrows(NoSuchQnaException.class, () -> qnaService.manageAnswer(1L, "답변 완료", 2));
    }

    @Test
    @DisplayName("Qna 서비스 : 파티장이 미답변 리스트를 성공적으로 조회한다.")
    void 미답변_문의내역_리스트_조회_성공() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaRepository.findAllByPartyAndQnaType(any(), any())).willReturn(testQnaListBuilder());

        // when
        List<QnaDTO> unansweredQuestion = qnaService.findAllUnansweredQuestion(1L);

        // then
        assertThat(unansweredQuestion.size()).isEqualTo(2);
        for (QnaDTO unanswered : unansweredQuestion) {
            assertThat(unanswered.getQnaType()).isEqualTo(QnaType.Q);
        }
    }

    @Test
    @DisplayName("Qna 서비스 : 파티장이 미답변 리스트 조회하는데 실패한다.")
    void 미답변_문의내역_리스트_조회_실패() {
        // given
        given(partyRepository.findByPartyId(anyLong())).willReturn(testPartyBuilder(1L));
        given(qnaRepository.findAllByPartyAndQnaType(any(), any())).willThrow(NoSuchQnaException.class);

        // then
        assertThrows(NoSuchQnaException.class, () -> qnaService.findAllUnansweredQuestion(1L));
    }

    private static Qna testQnaBuilder(Long qnaId) {
        return Qna.builder()
                .qnaId(qnaId)
                .party(testPartyBuilder(1L))
                .qnaYmd(LocalDateTime.now())
                .qnaType(QnaType.Q)
                .qnaSender("sender")
                .qnaStatus(1)
                .questionDetail("질문 내용")
                .build();
    }

    private static List<Qna> testQnaListBuilder() {
        List<Qna> qnaList = new ArrayList<>();
        qnaList.add(testQnaBuilder(1L));
        qnaList.add(testQnaBuilder(2L));

        return qnaList;
    }

    private static Party testPartyBuilder(Long partyId) {
        return Party.builder()
                .partyId(partyId)
                .leaderId("leader")
                .build();
    }
}