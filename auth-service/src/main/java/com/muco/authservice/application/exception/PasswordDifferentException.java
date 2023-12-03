package com.muco.authservice.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class PasswordDifferentException extends AuthException {

    public PasswordDifferentException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}