/*
package com.dev.nbbang.party.global.service;

import com.dev.nbbang.party.domain.party.dto.request.MatchingRequest;
import com.dev.nbbang.party.global.common.NotifyRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotifyProducerKafka {
    private final String TOPIC = "send-notify";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void sendNotify(NotifyRequest notifyRequest) throws JsonProcessingException {
        log.info("[NotifyProducer] Party Service -> Notify Service");
        String sendMessage = objectMapper.writeValueAsString(notifyRequest);

        log.info("[NotifyProducer] sendMessage : " + sendMessage);
        kafkaTemplate.send(TOPIC, sendMessage);
    }
}
*/
