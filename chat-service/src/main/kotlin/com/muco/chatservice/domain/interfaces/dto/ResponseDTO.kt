package com.muco.chatservice.domain.interfaces.dto

import com.fasterxml.jackson.annotation.JsonInclude
import reactor.core.publisher.Mono

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class ResponseDTO(
    val message: String?,
    val data: Any?
) {

    private constructor(message: String): this(message, null)

    companion object {
        fun of(message: String): Mono<ResponseDTO> = Mono.just(ResponseDTO(message))
        fun of(message: String, data: Any?): Mono<ResponseDTO> = Mono.just(ResponseDTO(message, data ?: RuntimeException("데이터가 null 일 수 없습니다.")))
    }
}
