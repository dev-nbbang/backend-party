package com.dev.nbbang.party.domain.payment.exception;

import com.dev.nbbang.party.global.exception.NbbangException;

public class FailImportServerException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailImportServerException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailImportServerException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

