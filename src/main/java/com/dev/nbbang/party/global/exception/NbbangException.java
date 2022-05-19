package com.dev.nbbang.party.global.exception;

public enum NbbangException {
    NO_CREATE_PARTY ("BE001", "Doesn't Create Party"),
    NOT_FOUND_PARTY("BE002", "Not Found Party"),
    NOT_FOUND_OTT("BE101", "Not Found Ott Platform"),;


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
