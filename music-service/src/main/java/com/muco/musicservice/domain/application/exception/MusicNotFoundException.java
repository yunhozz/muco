package com.muco.musicservice.domain.application.exception;

import com.muco.musicservice.global.enums.ErrorCode;

public class MusicNotFoundException extends MusicServiceException {

    public MusicNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
