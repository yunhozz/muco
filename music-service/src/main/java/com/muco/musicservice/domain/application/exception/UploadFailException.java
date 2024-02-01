package com.muco.musicservice.domain.application.exception;

import com.muco.musicservice.global.enums.ErrorCode;

public class UploadFailException extends MusicServiceException {

    public UploadFailException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
}
