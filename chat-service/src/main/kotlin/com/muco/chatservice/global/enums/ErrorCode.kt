package com.muco.chatservice.global.enums

enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String
) {
    INVALID_REQUEST(400, "M-001", "Invalid request"),
    INVALID_VALUE_TYPES(400, "M-002", "Invalid value types"),
    RESOURCE_NOT_FOUND(404, "M-004", "Resource not found"),
    METHOD_NOT_ALLOWED(405, "M-005", "Invalid Method"),
    PAYLOAD_TOO_LARGE(413, "M-013", "File size exceeds maximum limit"),
    UNSUPPORTED_MEDIA_TYPE(415, "M-015", "Media type not supported"),
    INTERNAL_SERVER_ERROR(500, "M-500", "Server Error");
}