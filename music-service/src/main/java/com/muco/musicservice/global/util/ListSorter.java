package com.muco.musicservice.global.util;

import java.util.List;

@FunctionalInterface
public interface ListSorter<T> {

    List<T> sortDescByNumberOfKeywords(String keyword, List<T> list);
}