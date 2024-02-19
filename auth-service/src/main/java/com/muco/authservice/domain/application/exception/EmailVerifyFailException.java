package com.muco.authservice.domain.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class EmailVerifyFailException extends AuthException {

    public EmailVerifyFailException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}