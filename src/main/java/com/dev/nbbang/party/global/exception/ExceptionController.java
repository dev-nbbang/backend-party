package com.dev.nbbang.party.global.exception;

import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import com.dev.nbbang.party.global.common.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(NoSuchQnaException.class)
    public ResponseEntity<CommonResponse> handleNotFoundQnaException(Exception e) {
        e.printStackTrace();

        return ResponseEntity.ok(CommonResponse.response(false, e.getMessage()));
    }

}
