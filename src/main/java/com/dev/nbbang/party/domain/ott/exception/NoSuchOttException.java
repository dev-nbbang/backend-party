package com.dev.nbbang.party.domain.ott.exception;


import com.dev.nbbang.party.global.exception.NbbangException;

public class NoSuchOttException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoSuchOttException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoSuchOttException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

