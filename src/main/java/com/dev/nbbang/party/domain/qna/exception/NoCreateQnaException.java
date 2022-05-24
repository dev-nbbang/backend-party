package com.dev.nbbang.party.domain.qna.exception;


import com.dev.nbbang.party.global.exception.NbbangCommonException;
import com.dev.nbbang.party.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class NoCreateQnaException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public NoCreateQnaException(String message, NbbangException nbbangException) {
        this.message = message;
        this.nbbangException = nbbangException;
    }


    @Override
    public String getErrorCode() {
        return nbbangException.getCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.OK;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

