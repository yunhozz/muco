package com.muco.authservice.domain.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    private String message;
    private Object data;

    private ResponseDTO(String message) {
        this.message = message;
    }

    private ResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO of(String message) {
        return new ResponseDTO(message);
    }

    public static <T> ResponseDTO of(String message, Object data, Class<T> clazz) {
        return new ResponseDTO(message, clazz.cast(data));
    }
}