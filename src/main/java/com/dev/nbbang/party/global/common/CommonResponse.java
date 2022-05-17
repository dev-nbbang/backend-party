package com.dev.nbbang.party.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommonResponse {
    private boolean status;
    private String message;

    @Builder
    public CommonResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CommonResponse response(boolean status, String message) {
        return CommonResponse.builder()
                .status(status)
                .message(message)
                .build();
    }
}
