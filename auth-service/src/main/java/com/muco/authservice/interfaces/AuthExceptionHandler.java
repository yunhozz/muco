package com.muco.authservice.interfaces;

import com.muco.authservice.application.exception.AuthException;
import com.muco.authservice.global.dto.res.ErrorResponseDTO;
import com.muco.authservice.global.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        log.error(e.getLocalizedMessage());
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        return ResponseEntity
                .internalServerError()
                .body(errorResponseDTO);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthException(AuthException e) {
        log.error(e.getLocalizedMessage());
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(e.getErrorCode(), e.getMessage());
        return ResponseEntity
                .status(errorResponseDTO.getStatus())
                .body(errorResponseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getLocalizedMessage());
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(ErrorCode.INVALID_REQUEST, e.getBindingResult());
        return ResponseEntity
                .badRequest()
                .body(errorResponseDTO);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpClientErrorException(HttpClientErrorException e) {
        log.error(e.getLocalizedMessage());
        return ResponseEntity
                .badRequest()
                .body(e.getResponseBodyAs(ErrorResponseDTO.class));
    }
}