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
import java.text.SimpleDateFormat;
import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class ImportAPI {
    private final RestTemplate restTemplate;

    private String paymentUrl = "https://api.iamport.kr/payments";
    private String billingPaymentUrl = "https://api.iamport.kr/subscribe/payments/again";
    private String scheduleUrl = "https://api.iamport.kr/subscribe/payments/schedule";
    private String impTokenUrl="https://api.iamport.kr/users/getToken";
    private String impKey="4738003304274604";
    private String impSecret="15dcccbe238c258980c02f4b4fd7d10060887cd418bc0ad086f36873856b7746c62d7739eef780d1";
    private String refundUrl = "https://api.iamport.kr/payments/cancel";
    private String unScheduleUrl = "https://api.iamport.kr/subscribe/payments/unschedule";

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
            if(!map.get("code").toString().equals("0")) throw new FailImportServerException("errorCode: " + map.get("code").toString() + "결제 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
            return (Map<String, Object>) map.get("response");
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public Map<String, Object> Payment(String accessToken, String billingKey, String merchant_uid, int price, String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", accessToken);
        log.info(accessToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_uid", billingKey);
        jsonObject.put("merchant_uid", merchant_uid);
        jsonObject.put("amount", price);
        jsonObject.put("name", name);

        log.info(String.valueOf(jsonObject));

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(billingPaymentUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                log.info(String.valueOf(map));
                if(!map.get("code").toString().equals("0")) throw new FailImportServerException("errorCode: " + map.get("code").toString() + "결제 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
                return (Map<String, Object>) map.get("response");
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public void Schedule(String accessToken, String billingKey, String merchant_uid, int price, String name) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", accessToken);
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.add(Calendar.MONTH, 1);
        long schedule_at = (now / 1000) + ((c.getTimeInMillis()-now)/1000);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

//        String[] merchantInfo = merchant_uid.split("-");
//        StringBuilder sb = new StringBuilder(merchantInfo[0] + "-" + merchantInfo[1] + "-" + sdf.format(c.getTime()) + "" + randomString());
        List<JSONObject> list = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_uid", billingKey);
        JSONObject schedule = new JSONObject();
        schedule.put("merchant_uid", merchant_uid);
        schedule.put("schedule_at", schedule_at);
        schedule.put("amount", price);
        schedule.put("name", name);
        list.add(schedule);
        jsonObject.put("schedules", list);

        log.info(String.valueOf(jsonObject));

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(scheduleUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                log.info(String.valueOf(map));
                if(!map.get("code").toString().equals("0")) throw new FailImportServerException("errorCode: " + map.get("code").toString() + "예약 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
                return;
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public Map<String, Object> Refund(String accessToken, String reason, String imp_uid, int amount, int cancelableAmount) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", accessToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reason", reason);
        jsonObject.put("imp_uid", imp_uid);
        jsonObject.put("amount", amount);
        jsonObject.put("checksum", cancelableAmount);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(refundUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                log.info(String.valueOf(map));
                if(!map.get("code").toString().equals("0")) throw new FailImportServerException("errorCode: " + map.get("code").toString() + "결제 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
                return (Map<String, Object>) map.get("response");
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public void unSchedule(String accessToken, String customerId, String merchantId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", accessToken);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customer_uid", customerId);
        List<String> list = new ArrayList<>();
        list.add(merchantId);
        jsonObject.put("merchant_uid", list);

        HttpEntity<String> impRequest = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        try {
            ResponseEntity<String> impResponse = restTemplate.postForEntity(unScheduleUrl, impRequest, String.class);
            if(impResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(impResponse.getBody(), Map.class);
                log.info(String.valueOf(map));
                if(!map.get("code").toString().equals("0")) throw new FailImportServerException("errorCode: " + map.get("code").toString() + "예약 취소 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
                return;
            }
        } catch (IOException e) {
            e.getMessage();
        }
        throw new FailImportServerException("server 접근 실패", NbbangException.FAIL_TO_IMPORT_SERVER);
    }

    public String randomString() {
        Random random = new Random();
        return random.ints(48, 123).filter(i -> (i<=57 || i>= 65) && (i<=90 || i>=97))
                .limit(6).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder:: append).toString();
    }

}