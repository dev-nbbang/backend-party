package com.dev.nbbang.party.domain.qna.exception;


import com.dev.nbbang.party.global.exception.NbbangException;

public class NoCreateQnaException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoCreateQnaException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoCreateQnaException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

