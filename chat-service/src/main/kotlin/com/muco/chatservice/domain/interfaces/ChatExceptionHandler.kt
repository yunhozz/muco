package com.muco.chatservice.domain.interfaces

import com.muco.chatservice.domain.interfaces.dto.ErrorResponseDTO
import com.muco.chatservice.global.enums.ErrorCode
import com.muco.chatservice.global.util.logger
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Mono

@RestControllerAdvice
class ChatExceptionHandler {

    private val log: Logger = logger()

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): Mono<ErrorResponseDTO> {
        log.error(e.localizedMessage)
        return ErrorResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR, e.localizedMessage)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): Mono<ErrorResponseDTO> {
        log.error(e.localizedMessage)
        return ErrorResponseDTO.of(ErrorCode.INVALID_REQUEST, e.bindingResult)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpClientErrorException(e: HttpClientErrorException): Mono<ErrorResponseDTO> {
        log.error(e.localizedMessage)
        return Mono.justOrEmpty(e.getResponseBodyAs(ErrorResponseDTO::class.java))
    }
}