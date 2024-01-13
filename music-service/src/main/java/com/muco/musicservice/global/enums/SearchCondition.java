package com.muco.musicservice.global.enums;

import lombok.Getter;

@Getter
public enum SearchCondition {

    LATEST("latest", "최신순"),
    ACCURACY("accuracy", "정확도순"),
    POPULARITY("popularity", "인기순"),
    ASCEND("ascend", "오름차순");

    private final String condition;
    private final String value;

    SearchCondition(String condition, String value) {
        this.condition = condition;
        this.value = value;
    }

    public static SearchCondition of(String condition) {
        for (SearchCondition searchCondition : SearchCondition.values()) {
            if (searchCondition.condition.equals(condition)) {
                return searchCondition;
            }
        }
        throw new IllegalArgumentException(String.format("%s에 해당하는 검색 조건이 존재하지 않습니다.", condition));
    }
}