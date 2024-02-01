package com.muco.musicservice.domain.application.exception;

import com.muco.musicservice.global.enums.ErrorCode;

public class DownloadFailException extends MusicServiceException {

    public DownloadFailException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
