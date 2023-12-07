package com.muco.authservice.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class EmailDuplicateException extends AuthException {

    public EmailDuplicateException(String message) {
        super(message, ErrorCode.INVALID_REQUEST);
    }
}