package com.muco.musicservice.domain.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseDTO(
        String message,
        Object data
) {
    private ResponseDTO(String message) {
        this(message, null);
    }

    public static ResponseDTO of(String message) {
        return new ResponseDTO(message);
    }

    public static <T> ResponseDTO of(String message, Object data, Class<T> clazz) {
        return new ResponseDTO(message, clazz.cast(data));
    }
}