package com.muco.musicservice.domain.application.exception;

import com.muco.musicservice.global.enums.ErrorCode;

public class PlaylistNotFoundException extends MusicServiceException {

    public PlaylistNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
