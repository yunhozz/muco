package com.muco.musicservice.global.enums;

import lombok.Getter;

@Getter
public enum SearchCategory {

    TOTAL("total", "통합검색"),
    MUSIC("music", "곡"),
    MUSICIAN("musician", "아티스트"),
    LYRICS("lyrics", "가사");

    private final String category;
    private final String value;

    SearchCategory(String category, String value) {
        this.category = category;
        this.value = value;
    }

    public static SearchCategory of(String category) {
        for (SearchCategory searchCategory : SearchCategory.values()) {
            if (searchCategory.category.equals(category)) {
                return searchCategory;
            }
        }
        throw new IllegalArgumentException(String.format("%s에 해당하는 검색 카테고리가 존재하지 않습니다.", category));
    }
}