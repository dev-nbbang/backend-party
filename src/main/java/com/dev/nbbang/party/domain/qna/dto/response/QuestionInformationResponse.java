package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
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
    private QnaType qnaType;
    private String qnaSender;
    private Integer qnaStatus;
    private String questionDetail;

    @Builder
    public QuestionInformationResponse(Long qnaId, Long partyId, LocalDateTime qnaYmd, QnaType qnaType, String qnaSender, Integer qnaStatus, String questionDetail) {
        this.qnaId = qnaId;
        this.partyId = partyId;
        this.qnaYmd = qnaYmd;
        this.qnaType = qnaType;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
    }

    public static QuestionInformationResponse create(QnaDTO qna) {
        return QuestionInformationResponse.builder()
                .qnaId(qna.getQnaId())
                .partyId(qna.getParty().getPartyId())
                .qnaYmd(qna.getQnaYmd())
                .qnaType(qna.getQnaType())
                .qnaSender(qna.getQnaSender())
                .qnaStatus(qna.getQnaStatus())
                .questionDetail(qna.getQuestionDetail())
                .build();
    }
}
