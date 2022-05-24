package com.dev.nbbang.party.global.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException{
    public abstract String getErrorCode();
    public abstract HttpStatus getHttpStatus();
    public abstract String getMessage();
}