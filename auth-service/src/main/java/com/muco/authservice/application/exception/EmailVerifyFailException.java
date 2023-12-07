package com.muco.authservice.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class EmailVerifyFailException extends AuthException {

    public EmailVerifyFailException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}