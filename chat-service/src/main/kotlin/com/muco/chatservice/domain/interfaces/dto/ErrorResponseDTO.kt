package com.muco.chatservice.domain.interfaces.dto

import com.muco.chatservice.global.enums.ErrorCode
import org.springframework.validation.BindingResult
import reactor.core.publisher.Mono

data class ErrorResponseDTO(
    val status: Int,
    val code: String,
    val message: String,
    val fieldErrors: List<FieldErrorResponseDTO>
) {
    private constructor(errorCode: ErrorCode): this(status = errorCode.status, code = errorCode.code, message = errorCode.message, fieldErrors = emptyList())
    private constructor(errorCode: ErrorCode, message: String): this(status = errorCode.status, code = errorCode.code, message, fieldErrors = emptyList())
    private constructor(errorCode: ErrorCode, fieldErrors: List<FieldErrorResponseDTO>): this(status = errorCode.status, code = errorCode.code, message = errorCode.message, fieldErrors)

    companion object {
        fun of(errorCode: ErrorCode): Mono<ErrorResponseDTO> = Mono.just(ErrorResponseDTO(errorCode))
        fun of(errorCode: ErrorCode, message: String): Mono<ErrorResponseDTO> = Mono.just(ErrorResponseDTO(errorCode, message))
        fun of(errorCode: ErrorCode, result: BindingResult): Mono<ErrorResponseDTO> = Mono.just(ErrorResponseDTO(errorCode, FieldErrorResponseDTO.of(result)))
    }

    data class FieldErrorResponseDTO(
        val field: String,
        val value: String,
        val reason: String?
    ) {
        companion object {
            fun of(field: String, value: String, reason: String): List<FieldErrorResponseDTO> = listOf(
                FieldErrorResponseDTO(field, value, reason)
            )

            fun of(result: BindingResult): List<FieldErrorResponseDTO> = result.fieldErrors.stream()
                    .map {
                        FieldErrorResponseDTO(
                            field = it.field,
                            value = (it.rejectedValue ?: "").toString(),
                            reason = it.defaultMessage)
                    }
                    .toList()
        }
    }
}
