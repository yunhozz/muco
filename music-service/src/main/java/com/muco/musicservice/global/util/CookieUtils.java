package com.muco.musicservice.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public final class CookieUtils {

    private CookieUtils() {}

    public static Optional<Cookie> getCookie(String name, HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    public static void addCookie(String name, String value, int ttl, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(ttl);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .ifPresent(cookie -> {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                });
    }
}