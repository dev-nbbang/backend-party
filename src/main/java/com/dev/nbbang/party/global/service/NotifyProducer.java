package com.dev.nbbang.party.global.service;

import com.dev.nbbang.party.global.common.NotifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotifyProducer {
    private final RabbitTemplate rabbitTemplate;

    private final String NOTIFY_EXCHANGE = "notify.exchange";       // 알림 보내는 경우 요청이 많은 것을 고려해서 새로운 Exchange로 사용
    private final String NOTIFY_ROUTING_KEY = "notify.route";

    public void sendNotify(NotifyRequest notifyRequest) {
        log.info("[NOTIFY QUEUE] message : {}", notifyRequest.toString());

        rabbitTemplate.convertAndSend(NOTIFY_EXCHANGE, NOTIFY_ROUTING_KEY, notifyRequest);
    }

}