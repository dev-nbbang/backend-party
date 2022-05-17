package com.dev.nbbang.party.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonSuccessResponse {
    private boolean status;
    private Object data;
    private String message;

    @Builder
    public CommonSuccessResponse(boolean status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static CommonSuccessResponse response(boolean status, Object data, String message) {
        return CommonSuccessResponse.builder()
                .status(status)
                .data(data)
                .message(message)
                .build();
    }
}
