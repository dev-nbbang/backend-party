package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class QuestionInformationResponse {
    private Long qnaId;
    private Long partyId;
    private LocalDateTime qnaYmd;
    private String qnaSender;
    private QnaStatus qnaStatus;
    private String questionDetail;

    @Builder
    public QuestionInformationResponse(Long qnaId, Long partyId, LocalDateTime qnaYmd, String qnaSender, QnaStatus qnaStatus, String questionDetail) {
        this.qnaId = qnaId;
        this.partyId = partyId;
        this.qnaYmd = qnaYmd;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
    }

    public static QuestionInformationResponse create(QnaDTO qna) {
        return QuestionInformationResponse.builder()
                .qnaId(qna.getQnaId())
                .partyId(qna.getParty().getPartyId())
                .qnaYmd(qna.getQnaYmd())
                .qnaSender(qna.getQnaSender())
                .qnaStatus(qna.getQnaStatus())
                .questionDetail(qna.getQuestionDetail())
                .build();
    }
}
