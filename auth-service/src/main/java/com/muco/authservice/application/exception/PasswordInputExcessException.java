package com.muco.authservice.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class PasswordInputExcessException extends AuthException {

    public PasswordInputExcessException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}