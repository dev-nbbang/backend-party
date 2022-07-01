package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.party.dto.request.MatchingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchingProducer {
    private final String TOPIC = "matching-fail";
    private final String TOPIC2 = "matching-success";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public void sendFailMatching(MatchingRequest matchingRequest) throws JsonProcessingException {
        log.info("[MemberProducer] Auth Service -> Member Service");
        String sendMessage = objectMapper.writeValueAsString(matchingRequest);

        log.info("[MemberProducer] sendMessage : " + sendMessage);
        kafkaTemplate.send(TOPIC, sendMessage);
    }

    @Transactional
    public void sendMatching(MatchingRequest matchingRequest) throws JsonProcessingException {
        log.info("[MemberProducer] Auth Service -> Member Service");
        String sendMessage = objectMapper.writeValueAsString(matchingRequest);

        log.info("[MemberProducer] sendMessage : " + sendMessage);
        kafkaTemplate.send(TOPIC2, sendMessage);
    }
}
