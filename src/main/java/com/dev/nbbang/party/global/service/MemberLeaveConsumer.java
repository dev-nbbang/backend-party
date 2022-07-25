package com.dev.nbbang.party.global.service;

import com.dev.nbbang.party.domain.payment.exception.FailDeletePaymentLogException;
import com.dev.nbbang.party.domain.payment.repository.PaymentLogRepository;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import com.dev.nbbang.party.global.common.MemberLeaveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class MemberLeaveConsumer {
    private final QnaRepository qnaRepository;
    private final PaymentLogRepository paymentLogRepository;

    private final String MEMBER_LEAVE_PARTY_QUEUE = "member.leave.party.queue";

    @Transactional
    @RabbitListener(queues = {MEMBER_LEAVE_PARTY_QUEUE})
    public void receiveMemberLeaveMessage(MemberLeaveResponse response) {
        log.info("[MEMBER LEAVE QUEUE Received Message : {}", response.toString());

        try {
            // 탈퇴한 회원 아이디로 QNA 전체 삭제
            qnaRepository.deleteAllByQnaSender(response.getMemberId());

            // 탈퇴한 회원 앙이디로 결제 이력 전체 삭제
            paymentLogRepository.deleteAllByMemberId(response.getMemberId());
        }
        // 로직 예외 발생 시 메세지 재처리 필요 (2회 시도 후 DLX 처리)
        catch (Exception e) {
            log.error("회원 탈퇴 메세지 예외 발생 메세지 재처리 필요");

            throw new IllegalArgumentException("회원 탈퇴 메세지 예외 발생 메세지 재처리 필요");
        }
    }
}
