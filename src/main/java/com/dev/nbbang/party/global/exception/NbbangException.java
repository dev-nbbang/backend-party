package com.dev.nbbang.party.global.exception;

public enum NbbangException {
    // 임의로 하나 만들어 놓음
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),;


    private String code;
    private String message;

    NbbangException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
