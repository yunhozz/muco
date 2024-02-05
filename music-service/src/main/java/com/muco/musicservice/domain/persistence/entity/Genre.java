package com.muco.musicservice.domain.persistence.entity;

public enum Genre {
    CLASSIC,
    JAZZ,
    POP,
    BALLAD,
    HIPHOP,
    COUNTRY,
    DISCO,
    ROCK,
    ELECTRONIC,
    TROT
    ;

    public static Genre of(String value) {
        for (Genre genre : Genre.values()) {
            if (genre.name().equals(value)) {
                return genre;
            }
        }
        throw new IllegalArgumentException(String.format("%s에 해당하는 장르가 존재하지 않습니다.", value));
    }
}