package com.muco.musicservice.global.util;

import java.util.List;

@FunctionalInterface
public interface ListSorter<T> {

    List<T> sort(String keyword, List<T> list);

    static int compareByNumberOfKeywords(String n1, String n2, String keyword, boolean ascending) {
        int count1 = n1.length() - n1.replace(keyword, "").length();
        int count2 = n2.length() - n2.replace(keyword, "").length();
        return ascending ? count1 - count2 : count2 - count1;
    }
}