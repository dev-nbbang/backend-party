package com.dev.nbbang.party.domain.ott.exception;


import com.dev.nbbang.party.global.exception.BaseException;
import com.dev.nbbang.party.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class NoSuchOttException extends BaseException {
    private final String message;
    private final NbbangException nbbangException;

    public NoSuchOttException(String message, NbbangException nbbangException) {
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

