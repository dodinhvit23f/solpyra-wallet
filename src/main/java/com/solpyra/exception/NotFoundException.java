package com.solpyra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends Exception {
    private final String errorCode;

    public NotFoundException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public NotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}