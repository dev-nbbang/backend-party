package com.dev.nbbang.party.domain.qna.service;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.Qna;

import java.util.List;

public interface QnaService {
    // 파티 문의 작성
    QnaDTO createQuestion(Qna qna);

    // 질문자가 문의한 파티 문의/답변 전체 조회
    List<QnaDTO> findAllQnA(Long partyId, String senderId);

    // 파티 문의 삭제
    boolean deleteQuestion(Long qnaId);

    // 파티 문의 수정
    QnaDTO modifyQuestion(Long qnaId, String questionDetail);

    // 파티 답변 작성/수정/삭제
    QnaDTO manageAnswer(Long qnaId, String answerDetail, Integer answerType);

    // 미답변 질문 리스트 목록 가져오기
    List<QnaDTO> findAllUnansweredQuestion(Long partyId);
}
