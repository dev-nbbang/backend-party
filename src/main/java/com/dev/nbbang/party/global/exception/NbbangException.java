package com.dev.nbbang.party.global.exception;

public enum NbbangException {
    // 임의로 하나 만들어 놓음
    NO_CREATE_PARTY ("BE001", "Doesn't Create Party"),
    NOT_FOUND_PARTY("BE002", "Not Found Party"),
    NOT_FOUND_OTT("BE101", "Not Found Ott Platform"),
    FAIL_TO_IMPORT_SERVER("BE203", "Failed To Import Server");

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
