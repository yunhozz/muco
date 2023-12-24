package com.muco.musicservice.interfaces.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDTO<T> {

    private String message;
    private T data;

    private ResponseDTO(String message) {
        this.message = message;
    }

    private ResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO<Object> of(String message) {
        return new ResponseDTO<>(message);
    }

    public static <T> ResponseDTO<T> of(String message, T data) {
        return new ResponseDTO<>(message, data);
    }
}