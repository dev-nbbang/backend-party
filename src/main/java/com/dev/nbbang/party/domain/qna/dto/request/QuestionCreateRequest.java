package com.dev.nbbang.party.domain.qna.dto.request;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class QuestionCreateRequest {
    private Long partyId;
    private String qnaSender;
    private String questionDetail;

    @Builder
    public QuestionCreateRequest(Long partyId, String qnaSender, String questionDetail) {
        this.partyId = partyId;
        this.qnaSender = qnaSender;
        this.questionDetail = questionDetail;
    }

    public static Qna toEntity(QuestionCreateRequest request, PartyDTO party) {
        return Qna.builder()
                .party(PartyDTO.toEntity(party))
                .qnaYmd(LocalDateTime.now())
                .qnaSender(request.getQnaSender())
                .questionDetail(request.getQuestionDetail())
                .build();
    }
}
