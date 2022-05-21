package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class QnaListResponse {
    private Long qnaId;
    private LocalDateTime qnaYmd;
    private QnaType qnaType;
    private String qnaSender;
    private Integer qnaStatus;
    private String questionDetail;
    private String answerDetail;

    @Builder
    public QnaListResponse(Long qnaId, LocalDateTime qnaYmd, QnaType qnaType, String qnaSender, Integer qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.qnaYmd = qnaYmd;
        this.qnaType = qnaType;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
        this.answerDetail = answerDetail;
    }

    public static List<QnaListResponse> createList(List<QnaDTO> qnaList) {
        List<QnaListResponse> response = new ArrayList<>();
        for (QnaDTO qna : qnaList) {
            response.add(QnaListResponse.builder()
                    .qnaId(qna.getQnaId())
                    .qnaYmd(qna.getQnaYmd())
                    .qnaType(qna.getQnaType())
                    .qnaSender(qna.getQnaSender())
                    .qnaStatus(qna.getQnaStatus())
                    .questionDetail(qna.getQuestionDetail())
                    .answerDetail(qna.getAnswerDetail())
                    .build());
        }
        return response;
    }
}
