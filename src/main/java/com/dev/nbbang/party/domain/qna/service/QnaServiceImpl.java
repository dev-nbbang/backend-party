package com.dev.nbbang.party.domain.qna.service;

import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.AnswerType;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoCreateQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.dev.nbbang.party.global.service.NotifyProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QnaServiceImpl implements QnaService {
    private final QnaRepository qnaRepository;
    private final PartyRepository partyRepository;

    /**
     * 질문자가 파티장에게 문의사항을 남긴다.
     *
     * @param question 질문자가 질문한 내용을 담은 데이터
     * @return 문의한 질문을 담은 데이터
     */
    @Override
    @Transactional
    public QnaDTO createQuestion(Qna question) {
        // 1. 문의사항 저장
        Qna savedQuestion = Optional.of(qnaRepository.save(question))
                .orElseThrow(() -> new NoCreateQnaException("문의 등록에 실패했습니다.", NbbangException.NO_CREATE_QUESTION));

        return QnaDTO.create(savedQuestion);
    }

    /**
     * 질문자가 해당 파티에 남긴 모든 문의내역을 조회한다.
     *
     * @param partyId  조회할 파티의 고유 아이디
     * @param senderId 질문자 아이디
     * @return 해당 파티에 남긴 모든 문의내역 리스트
     */
    @Override
    public List<QnaDTO> findAllQnA(Long partyId, String senderId) {
        // 1. 파티 아이디를 통해 조회하고자 하는 파티를 찾는다.
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        // 2. 질문자 아이디와 파티를 이용해 질문자가 문의한 모든 문의사항을 조회한다.
        List<Qna> findQnaList = qnaRepository.findAllByPartyAndQnaSender(findParty, senderId);

        if (findQnaList.isEmpty()) throw new NoSuchQnaException("등록된 문의 내역이 없습니다.", NbbangException.NOT_FOUND_QNA);

        return QnaDTO.createList(findQnaList);
    }

    /**
     * 질문자가 문의한 내역을 삭제한다.
     *
     * @param qnaId 삭제할 문의 아이디
     */
    @Override
    @Transactional
    public boolean deleteQuestion(Long qnaId) {
        // 1. 문의내역 아이디로 문의내역 삭제
        qnaRepository.deleteByQnaId(qnaId);

        // 2. 삭제한 문의내역 아이디로 삭제 확인 -> 삭제 안된 경우 예외처리
        Optional.ofNullable(qnaRepository.findByQnaId(qnaId)).ifPresent(
                exception -> {
                    throw new FailDeleteQnaException("문의내역 삭제에 실패했습니다.", NbbangException.FAIL_TO_DELETE_QNA);
                }
        );

        return true;
    }

    /**
     * 질문자가 문의내역을 수정한다.
     *
     * @param qnaId          수정할 문의내역 아이디
     * @param questionDetail 문의 내용
     * @return 수정한 문의내역을 담은 데이터
     */
    @Override
    @Transactional
    public QnaDTO modifyQuestion(Long qnaId, String questionDetail) {
        // 1. 문의내역 아이디로 수정할 문의내역 가져오기
        Qna updatedQna = Optional.ofNullable(qnaRepository.findByQnaId(qnaId))
                .orElseThrow(() -> new NoSuchQnaException("등록되지 않았거나 삭제된 질문자에 의해 삭제된 문의내역입니다.", NbbangException.NOT_FOUND_QNA));

        // 2. 문의내역 수정하기
        updatedQna.modifyQuestion(questionDetail);

        return QnaDTO.create(updatedQna);
    }

    /**
     * 파티장이 문의내역에 대해 답변을 관리한다.
     *
     * @param qnaId        문의내역 아이디
     * @param answerDetail 문의 답변 내용
     * @param answerType   0 : 해당 문의에 대한 답변, 1 : 해당 문의에 대한 답변 수정, 2 : 해당 문의에 대한 답변 삭제(빈칸으로 수정)
     * @return 답변 이후의 문의내역 데이터
     */
    @Override
    @Transactional
    public QnaDTO manageAnswer(Long qnaId, String answerDetail, AnswerType answerType) {
        // 1. 관리할 문의내역을 불러온다
        Qna managedQna = Optional.ofNullable(qnaRepository.findByQnaId(qnaId))
                .orElseThrow(() -> new NoSuchQnaException("등록되지 않았거나 삭제된 질문자에 의해 삭제된 문의내역입니다.", NbbangException.NOT_FOUND_QNA));

        // 2. 문의에 대한 답변에 따라 다르게
        if (answerType != AnswerType.DELETE) {
            // 문의에 대한 새로운 답변 ( 1 -> 2)
            managedQna.answerQuestion(answerDetail);
        } else {
            // 기존 답변 빈칸으로 수정 ( 2 -> 1)
            managedQna.deleteAnswer();
        }

        return QnaDTO.create(managedQna);
    }

    /**
     * 해당 파티의 미답변 질문 리스트를 가져온다.
     *
     * @param partyId 파티 고유 아이디
     * @return 미답변 질문 리스트 데이터
     */
    @Override
    public List<QnaDTO> findAllUnansweredQuestion(Long partyId) {
        // 1. 조회핲 파티 조회하기.
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        // 2. 해당 파티의 미답변 질문 리스트를 가져온다.
        List<Qna> unansweredQuestions = qnaRepository.findAllByPartyAndQnaStatus(findParty, QnaStatus.Q);

        if (unansweredQuestions.isEmpty())
            throw new NoSuchQnaException("답변하지 않은 문의 내역이 없습니다.", NbbangException.NOT_FOUND_QNA);

        return QnaDTO.createList(unansweredQuestions);
    }
}
