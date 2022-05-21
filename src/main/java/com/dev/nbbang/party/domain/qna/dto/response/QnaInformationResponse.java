package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class QnaInformationResponse {
    private Long qnaId;
    private Long partyId;
    private LocalDateTime qnaYmd;
    private QnaType qnaType;
    private String qnaSender;
    private Integer qnaStatus;
    private String questionDetail;
    private String answerDetail;

    @Builder
    public QnaInformationResponse(Long qnaId, Long partyId, LocalDateTime qnaYmd, QnaType qnaType, String qnaSender, Integer qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.partyId = partyId;
        this.qnaYmd = qnaYmd;
        this.qnaType = qnaType;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
        this.answerDetail = answerDetail;
    }

    public static QnaInformationResponse create(QnaDTO qna) {
        return QnaInformationResponse.builder()
                .qnaId(qna.getQnaId())
                .partyId(qna.getParty().getPartyId())
                .qnaYmd(qna.getQnaYmd())
                .qnaType(qna.getQnaType())
                .qnaSender(qna.getQnaSender())
                .qnaStatus(qna.getQnaStatus())
                .questionDetail(qna.getQuestionDetail())
                .answerDetail(qna.getAnswerDetail())
                .build();
    }
}
