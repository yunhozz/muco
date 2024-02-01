package com.muco.musicservice.domain.application.exception;

import com.muco.musicservice.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class MusicServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public MusicServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
