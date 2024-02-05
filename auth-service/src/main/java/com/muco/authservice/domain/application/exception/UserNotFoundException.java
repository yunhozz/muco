package com.muco.authservice.domain.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}