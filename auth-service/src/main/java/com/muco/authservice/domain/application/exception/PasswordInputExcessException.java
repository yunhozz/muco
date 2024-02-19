package com.muco.authservice.domain.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class PasswordInputExcessException extends AuthException {

    public PasswordInputExcessException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}