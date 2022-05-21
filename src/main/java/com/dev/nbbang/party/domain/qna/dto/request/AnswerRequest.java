package com.dev.nbbang.party.domain.qna.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRequest {
    private String answerDetail;

    @Builder
    public AnswerRequest(String answerDetail) {
        this.answerDetail = answerDetail;
    }
}
