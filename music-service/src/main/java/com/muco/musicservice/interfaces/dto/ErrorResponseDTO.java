package com.muco.musicservice.interfaces.dto;

import com.muco.musicservice.global.enums.ErrorCode;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ErrorResponseDTO(
        int status,
        String code,
        String message,
        List<FieldErrorResponseDTO> fieldErrors
) {
    private ErrorResponseDTO(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), new ArrayList<>());
    }

    private ErrorResponseDTO(ErrorCode errorCode, String message) {
        this(errorCode.getStatus(), errorCode.getCode(), message, new ArrayList<>());
    }

    private ErrorResponseDTO(ErrorCode errorCode, List<FieldErrorResponseDTO> fieldErrors) {
        this(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), fieldErrors);
    }

    public static ErrorResponseDTO of(ErrorCode errorCode) {
        return new ErrorResponseDTO(errorCode);
    }

    public static ErrorResponseDTO of(ErrorCode errorCode, String message) {
        return new ErrorResponseDTO(errorCode, message);
    }

    public static ErrorResponseDTO of(ErrorCode errorCode, BindingResult result) {
        return new ErrorResponseDTO(errorCode, FieldErrorResponseDTO.of(result));
    }

    private record FieldErrorResponseDTO(
            String field,
            String value,
            String reason
    ) {
        private static List<FieldErrorResponseDTO> of(String field, String value, String reason) {
            return new ArrayList<>() {{
                add(new FieldErrorResponseDTO(field, value, reason));
            }};
        }

        private static List<FieldErrorResponseDTO> of(BindingResult result) {
            return result.getFieldErrors().stream()
                    .map(fieldError -> new FieldErrorResponseDTO(
                            fieldError.getField(),
                            fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "",
                            fieldError.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}