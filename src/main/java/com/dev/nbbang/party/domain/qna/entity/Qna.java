package com.dev.nbbang.party.domain.qna.entity;

import com.dev.nbbang.party.domain.party.entity.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "QNA")
@Getter
@NoArgsConstructor
@DynamicInsert
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QNA_ID")
    private Long qnaId;

    @ManyToOne
    @JoinColumn(name = "PARTY_ID")
    private Party party;

    @Column(name = "QNA_YMD")
    private LocalDateTime qnaYmd;

    @Column(name = "QNA_SENDER")
    private String qnaSender;

    @Column(name = "QNA_STATUS")
    @Enumerated(EnumType.STRING)
    private QnaStatus qnaStatus;

    @Column(name = "QUESTION_DETAIL")
    private String questionDetail;

    @Column(name = "ANSWER_DETAIL")
    private String answerDetail;

    @Builder
    public Qna(Long qnaId, Party party, LocalDateTime qnaYmd, String qnaSender, QnaStatus qnaStatus, String questionDetail, String answerDetail) {
        this.qnaId = qnaId;
        this.party = party;
        this.qnaYmd = qnaYmd;
        this.qnaSender = qnaSender;
        this.qnaStatus = qnaStatus;
        this.questionDetail = questionDetail;
        this.answerDetail = answerDetail;
    }

    // 질문자가 문의 내용 수정
    public void modifyQuestion(String questionDetail) {
        this.questionDetail = questionDetail;
        this.qnaYmd = LocalDateTime.now();
    }

    // 답변자가 답변하거나 수정하는 경우
    public void answerQuestion(String answerDetail) {
        this.answerDetail = answerDetail;
        this.qnaStatus = QnaStatus.A;
    }

    // 답변자가 답변을 빈칸으로 변경하는 경우
    public void deleteAnswer() {
        this.answerDetail = null;
        this.qnaStatus = QnaStatus.Q;
    }
}
