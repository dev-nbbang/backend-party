package com.dev.nbbang.party.domain.qna.dto;

import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class QnaDTO {
    private Long qnaId;
    private Party party;
    private LocalDateTime qnaYmd;
    private QnaType qnaType;
    private String qnaSender;
    private Integer qnaStatus;
    private String questionDetail;
    private String answerDetail;

    @Builder
    public QnaDTO(Long qnaId, Party party, LocalDateTime qnaYmd, QnaType qnaType, String qnaSender, Integer qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.party = party;
        this.qnaYmd = qnaYmd;
        this.qnaType = qnaType;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
        this.answerDetail = answerDetail;
    }

    public static QnaDTO create(Qna qna) {
        return QnaDTO.builder()
                .qnaId(qna.getQnaId())
                .party(qna.getParty())
                .qnaYmd(qna.getQnaYmd())
                .qnaType(qna.getQnaType())
                .qnaSender(qna.getQnaSender())
                .qnaStatus(qna.getQnaStatus())
                .questionDetail(qna.getQuestionDetail())
                .answerDetail(qna.getAnswerDetail()).build();
    }

    public static List<QnaDTO> createList(List<Qna> qnaList) {
        List<QnaDTO> qnaDTOS = new ArrayList<>();

        for (Qna qna : qnaList) {
            qnaDTOS.add(QnaDTO.builder()
                    .qnaId(qna.getQnaId())
                    .party(qna.getParty())
                    .qnaYmd(qna.getQnaYmd())
                    .qnaType(qna.getQnaType())
                    .qnaSender(qna.getQnaSender())
                    .qnaStatus(qna.getQnaStatus())
                    .questionDetail(qna.getQuestionDetail())
                    .answerDetail(qna.getAnswerDetail()).build());
        }

        return qnaDTOS;
    }
}
