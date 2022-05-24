package com.dev.nbbang.party.domain.qna.repository;

import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    // 파티 문의 작성
    Qna save(Qna qnA);

    // 파티 문의/답변 전체 가져오기
    List<Qna> findAllByPartyAndQnaSender(Party party, String qnaSender);

    // 파티 문의 삭제
    void deleteByQnaId(Long qnaId);

    // 파티 문의 수정

    // 파티 답변 작성/수정/삭제

    // QnaId를 이용해 문의내역 조회히기 (삭제 검증 용도)
    Qna findByQnaId(Long qnaId);

    List<Qna> findAllByPartyAndQnaStatus(Party party, QnaStatus qnaStatus);
}
