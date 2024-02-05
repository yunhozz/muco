package com.muco.authservice.domain.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class MailSendFailException extends AuthException {

    public MailSendFailException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
}