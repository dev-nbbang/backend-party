package com.dev.nbbang.party.domain.payment.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpToken {
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Response {
        @JsonProperty("access_token")
        private String accessToken;
    }
}
