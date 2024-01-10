package com.muco.musicservice.global.enums;

import lombok.Getter;

@Getter
public enum SearchOrder {

    ACCURACY("accuracy", "정확도"),
    POPULARITY("popularity", "인기순"),
    LATEST("latest", "최신순"),
    ASC("asc", "가나다순");

    private final String order;
    private final String value;

    SearchOrder(String order, String value) {
        this.order = order;
        this.value = value;
    }

    public static SearchOrder of(String value) {
        for (SearchOrder searchOrder : SearchOrder.values()) {
            if (searchOrder.value.equals(value)) {
                return searchOrder;
            }
        }
        throw new IllegalArgumentException(String.format("%s에 해당하는 정렬 방식이 존재하지 않습니다.", value));
    }
}