package com.dev.nbbang.party.domain.party.exception;


import com.dev.nbbang.party.global.exception.NbbangCommonException;
import com.dev.nbbang.party.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class NoCreatePartyException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public NoCreatePartyException(String message, NbbangException nbbangException) {
        super(message);
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

