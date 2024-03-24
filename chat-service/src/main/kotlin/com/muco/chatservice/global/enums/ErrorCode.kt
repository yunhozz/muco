package com.muco.chatservice.global.enums

enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String
) {
    INVALID_REQUEST(400, "C-001", "Invalid request"),
    INVALID_VALUE_TYPES(400, "C-002", "Invalid value types"),
    RESOURCE_NOT_FOUND(404, "C-004", "Resource not found"),
    METHOD_NOT_ALLOWED(405, "C-005", "Invalid Method"),
    PAYLOAD_TOO_LARGE(413, "C-013", "File size exceeds maximum limit"),
    UNSUPPORTED_MEDIA_TYPE(415, "C-015", "Media type not supported"),
    INTERNAL_SERVER_ERROR(500, "C-500", "Server Error");
}