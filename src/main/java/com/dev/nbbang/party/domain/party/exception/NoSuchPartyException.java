package com.dev.nbbang.party.domain.party.exception;


import com.dev.nbbang.party.global.exception.NbbangException;

public class NoSuchPartyException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoSuchPartyException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoSuchPartyException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

