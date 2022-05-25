package com.dev.nbbang.party.domain.payment.api.service;

import com.dev.nbbang.party.domain.payment.api.dto.request.ImpToken;
import com.dev.nbbang.party.domain.payment.exception.FailImportServerException;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class ImportAPI {
    private final RestTemplate restTemplate;

    private final String paymentUrl = "https://api.iamport.kr/payments";
    private final String billingPaymentUrl = "https://api.iamport.kr/subscribe/payments/again";
    private final String scheduleUrl = "https://api.iamport.kr/subscribe/payments/schedule";
    private String impTokenUrl;
    private String impKey;
    private String impSecret;

    public String getAccessToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imp_key", impKey);
        jsonObject.put("imp_secret", impSecret);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(impTokenUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                ImpToken getBody = mapper.readValue(impResponse.getBody(), ImpToken.class);
                return getBody.getResponse().getAccessToken();
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public Map<String, Object> getPaymentInfo(String accessToken, String imp_uid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken);

        HttpEntity impRequest = new HttpEntity<>(httpHeaders);
        String uri = paymentUrl + "/" + imp_uid;

        try {
            ResponseEntity<String> impResponse = restTemplate.exchange(uri, HttpMethod.GET, impRequest, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
            return map;
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public Map<String, Object> Payment(String accessToken, String billingKey, String merchant_uid, int price, String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_uid", billingKey);
        jsonObject.put("merchant_uid", merchant_uid);
        jsonObject.put("amount", price);
        jsonObject.put("name", name);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(billingPaymentUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                return map;
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public Map<String, Object> Schedule(String accessToken, String billingKey, String merchant_uid, int price, String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken);
        Calendar c = Calendar.getInstance();
        long schedule_at = (c.getTimeInMillis() / 1000) + 2629743;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_uid", billingKey);
        JSONObject schedule = new JSONObject();
        schedule.put("merchant_uid", merchant_uid);
        schedule.put("schedule_at", schedule_at);
        schedule.put("amount", price);
        schedule.put("name", name);
        jsonObject.put("schedules", schedule);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(scheduleUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                return map;
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

}