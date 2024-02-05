package com.muco.musicservice.global.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(400, "M-001", "Invalid request"),
    INVALID_VALUE_TYPES(400, "M-002", "Invalid value types"),
    RESOURCE_NOT_FOUND(404, "M-004", "Resource not found"),
    METHOD_NOT_ALLOWED(405, "M-005", "Invalid Method"),
    PAYLOAD_TOO_LARGE(413, "M-013", "File size exceeds maximum limit"),
    UNSUPPORTED_MEDIA_TYPE(415, "M-015", "Media type not supported"),
    INTERNAL_SERVER_ERROR(500, "M-500", "Server Error");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}