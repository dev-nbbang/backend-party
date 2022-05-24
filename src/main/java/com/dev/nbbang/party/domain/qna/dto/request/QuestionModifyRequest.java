package com.dev.nbbang.party.domain.qna.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionModifyRequest {
    private String questionDetail;

    @Builder
    public QuestionModifyRequest(String questionDetail) {
        this.questionDetail = questionDetail;
    }
}
