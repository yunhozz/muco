package com.muco.authservice.application.exception;

import com.muco.authservice.global.enums.ErrorCode;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}