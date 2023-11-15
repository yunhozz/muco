package com.muco.authservice.interfaces;

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
public class UserExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        return ResponseEntity
                .internalServerError()
                .body(errorResponseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(ErrorCode.INVALID_REQUEST, e.getBindingResult());
        return ResponseEntity
                .badRequest()
                .body(errorResponseDTO);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpClientErrorException(HttpClientErrorException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(ErrorCode.METHOD_NOT_ALLOWED, e.getLocalizedMessage());
        return ResponseEntity
                .badRequest()
                .body(errorResponseDTO);
    }
}