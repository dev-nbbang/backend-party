package com.dev.nbbang.party.domain.qna.dto.response;

import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.entity.QnaStatus;
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
    private String qnaSender;
    private QnaStatus qnaStatus;
    private String questionDetail;
    private String answerDetail;

    @Builder
    public QnaListResponse(Long qnaId, LocalDateTime qnaYmd, String qnaSender, QnaStatus qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.qnaYmd = qnaYmd;
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
                    .qnaSender(qna.getQnaSender())
                    .qnaStatus(qna.getQnaStatus())
                    .questionDetail(qna.getQuestionDetail())
                    .answerDetail(qna.getAnswerDetail())
                    .build());
        }
        return response;
    }
}
