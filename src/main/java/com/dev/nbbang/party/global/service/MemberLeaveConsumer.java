package com.dev.nbbang.party.global.service;

import com.dev.nbbang.party.domain.payment.exception.FailDeletePaymentLogException;
import com.dev.nbbang.party.domain.payment.repository.PaymentLogRepository;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import com.dev.nbbang.party.global.common.MemberLeaveResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberLeaveConsumer {
    private final QnaRepository qnaRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "leave-member", groupId = "party-group-id")
    public void receiverLeaveMemberMessage(String message, Acknowledgment ack) throws JsonProcessingException {
        log.info("[Leave Member Message Receive] : 회원 탈퇴 이벤트 수신 (파티 서비스)");
        log.info("[Received Message] : " + message);

        MemberLeaveResponse response = objectMapper.readValue(message, MemberLeaveResponse.class);

        try {
            // 회원 아이디로 QNA 전체 삭제
            qnaRepository.deleteAllByQnaSender(response.getMemberId());
            try {
                // 회원 아이디로 결제이력 전체 삭제
                paymentLogRepository.deleteAllByMemberId(response.getMemberId());

                // 모두 삭제 완료된 경우 응답 전송
                ack.acknowledge();
            } catch (FailDeletePaymentLogException e) {
                log.info(response.getMemberId() + "님의 결제 이력 전체 삭제 실패");
            }
        } catch (FailDeleteQnaException e) {
            log.warn(response.getMemberId() + "님의 QNA 전체 삭제 실패");
        }
    }
}
