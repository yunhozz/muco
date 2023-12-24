package com.muco.musicservice.interfaces.dto;

import com.muco.musicservice.global.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDTO {

    private int status;
    private String code;
    private String message;
    private List<FieldErrorResponseDTO> fieldErrors;

    private ErrorResponseDTO(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.fieldErrors = new ArrayList<>();
    }

    private ErrorResponseDTO(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = message;
        this.fieldErrors = new ArrayList<>();
    }

    private ErrorResponseDTO(ErrorCode errorCode, List<FieldErrorResponseDTO> fieldErrors) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.fieldErrors = fieldErrors;
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

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class FieldErrorResponseDTO {

        private String field;
        private String value;
        private String reason;

        private FieldErrorResponseDTO() {}

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