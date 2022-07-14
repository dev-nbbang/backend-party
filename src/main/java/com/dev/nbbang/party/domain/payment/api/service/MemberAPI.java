package com.dev.nbbang.party.domain.payment.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberAPI {
    private final RestTemplate restTemplate;

    private final String memberUrl = "http://localhost:9000/nbbang-user/members/discount";

    // Member API에서 할인 가능 여부 확인 및 적용
    public boolean discount(String memberId, Long point, Integer couponId, Integer couponType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("couponId", couponId);
        jsonObject.put("couponType", couponType);
        JSONObject subObject = new JSONObject();
        subObject.put("memberId", memberId);
        subObject.put("usePoint", point);
        subObject.put("pointDetail", "결제 할인 적용 했습니다");
        subObject.put("pointType", "DECREASE");
        jsonObject.put("pointObj", subObject);

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(memberUrl, request, String.class);
        try {
            if(response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, Object> map = objectMapper.readValue(response.getBody(), Map.class);
                return (boolean) map.get("status");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
