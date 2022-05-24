package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
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
    private String qnaSender;
    private QnaStatus qnaStatus;
    private String questionDetail;
    private String answerDetail;

    @Builder
    public QnaInformationResponse(Long qnaId, Long partyId, LocalDateTime qnaYmd, String qnaSender, QnaStatus qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.partyId = partyId;
        this.qnaYmd = qnaYmd;
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
                .qnaSender(qna.getQnaSender())
                .qnaStatus(qna.getQnaStatus())
                .questionDetail(qna.getQuestionDetail())
                .answerDetail(qna.getAnswerDetail())
                .build();
    }
}
