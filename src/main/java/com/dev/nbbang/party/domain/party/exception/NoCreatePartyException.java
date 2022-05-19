package com.dev.nbbang.party.domain.party.exception;


import com.dev.nbbang.party.global.exception.NbbangException;

public class NoCreatePartyException extends RuntimeException {
    private final NbbangException nbbangException;

    public NoCreatePartyException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public NoCreatePartyException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

