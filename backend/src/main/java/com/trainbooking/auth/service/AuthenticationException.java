package com.trainbooking.auth.service;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public AuthenticationException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }
}
