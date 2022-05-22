package com.dev.nbbang.party.domain.qna.repository;

import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QnaRepositoryTest {
    @Autowired
    private QnaRepository qnaRepository;

    @Test
    @DisplayName("QnA 레포지토리 : 질문자가 문의한 내역을 성공적으로 저장한다")
    void 질문_내역_저장_성공() {
        // given
        Qna question = testQnaBuilder(1L);

        // when
        Qna savedQuestion = qnaRepository.save(testQnaBuilder(1L));
        Qna findQuestion = qnaRepository.findByQnaId(savedQuestion.getQnaId());

        // then
        assertThat(savedQuestion.getQnaType()).isEqualTo(findQuestion.getQnaType());
        assertThat(savedQuestion.getQnaSender()).isEqualTo(findQuestion.getQnaSender());
        assertThat(savedQuestion.getQnaId()).isEqualTo(findQuestion.getQnaId());
        assertThat(savedQuestion.getQnaStatus()).isEqualTo(findQuestion.getQnaStatus());
    }

    @Test
    @DisplayName("QnA 레포지토리 : 해당 파티에 질문자가 문의한 모든 문의내역을 성공적으로 조회한다.")
    void 질문자의_모든_문의내역_조회_성공() {
        // given
        String sender = "sender";
        Party party = testPartyBuilder(1L);

        // when
        qnaRepository.save(testQnaBuilder(1L));
        qnaRepository.save(testQnaBuilder(2L));

        List<Qna> questionList = qnaRepository.findAllByPartyAndQnaSender(party, sender);

        // then
        assertThat(questionList.size()).isEqualTo(2);
        for (Qna question : questionList) {
            assertThat(question.getQnaSender()).isEqualTo(sender);
        }
    }

    @Test
    @DisplayName("QnA 레포지토리 : 해당 파티에 질문자가 문의한 모든 문의내역을 조회하는데 실패한다.")
    void 질문자의_모든_문의내역_조회_실패() {
        // given
        String sender = "sender";
        Party party = testPartyBuilder(2L);

        // when
        List<Qna> questionList = qnaRepository.findAllByPartyAndQnaSender(party, sender);

        // then
        assertThat(questionList).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("QnA 레포지토리 : 파티 문의 내역을 성공적으로 삭제한다.")
    void 문의_내역_삭제_성공() {
        // given
        Long qnaId = 1L;

        // when
        qnaRepository.save(testQnaBuilder(qnaId));
        qnaRepository.deleteByQnaId(qnaId);
        Qna afterDeleteQna = qnaRepository.findByQnaId(qnaId);

        // then
        assertThat(afterDeleteQna).isNull();
    }

//    @Test
//    @DisplayName("QnA 레포지토리 : 미답변 문의 내역 리스트를 성공적으로 조회한다.")
//    void 미답변_문의_내역_조회_성공() {
//        // given
//        Party party = testPartyBuilder(1L);
//
//        // when
//        qnaRepository.save(testQnaBuilder(1L));
//        qnaRepository.save(testQnaBuilder(2L));
//
//        List<Qna> findUnansweredQuestions = qnaRepository.findAllByPartyAndQnaType(party, QnaType.Q);
//
//        assertThat(findUnansweredQuestions.size()).isEqualTo(2);
//    }

    @Test
    @DisplayName("QnA 레포지토리 : 미답변 문의 내역 리스트를 조회하는데 실패한다.")
    void 미답변_문의_내역_조회_실패() {
        // given
        Party party = testPartyBuilder(2L);

        // when
        List<Qna> findUnansweredQuestions = qnaRepository.findAllByPartyAndQnaType(party, QnaType.Q);

        // then
        assertThat(findUnansweredQuestions).isEqualTo(Collections.emptyList());
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