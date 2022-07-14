package com.dev.nbbang.party.global.exception;

public enum NbbangException {
    NO_CREATE_PARTY ("BE001", "Doesn't Create Party"),
    NOT_FOUND_PARTY("BE002", "Not Found Party"),
    INVALID_LEADER_GRANT("BE003", "Invalid Leader Grant"),
    FAIL_TO_DELETE_PARTY("BE004", "Fail To Delete Party"),
    DUPLICATE_OTT_ACC("BE005", "Duplicate Ott Acc Exception"),
    FAIL_TO_DELETE_PARTICIPANT("BE006", "Fail To Delete Participants"),
    NO_CREATE_PARTICIPANT("BE007", "Doesn't Create Participant"),
    NOT_FOUND_PARTICIPANT("BE008", "Not Found Participant"),
    ALREADY_JOIN_PARTY("BE009", "Already Join Party"),
    NO_JOIN_PARTY("BE010", "FULL Party"),
    NOT_FOUND_OTT("BE101", "Not Found Ott Platform"),
    NO_CREATE_OTT("BE102", "Doesn't Create Ott Platform"),
    FAIL_TO_DELETE_OTT("BE103", "Fail To Delete OTT"),
    NO_CREATE_QUESTION("BE201", "Doesn't Create Question"),
    NOT_FOUND_QNA("BE202", "Not Found Qna"),
    FAIL_TO_DELETE_QNA("BE203", "Fail To Delete Qna"),
    FAIL_TO_IMPORT_SERVER("BE301", "Failed To Import Server");


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
