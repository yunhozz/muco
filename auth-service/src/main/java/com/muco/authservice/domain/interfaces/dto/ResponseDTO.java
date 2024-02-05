package com.muco.authservice.domain.interfaces.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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