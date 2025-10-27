package com.example.authtest.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static void addAccessTokenCookie(HttpServletResponse res, String token, int maxAgeSeconds, boolean secure) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure); // false on http://localhost, true in prod HTTPS
        cookie.setPath("/");
        // SameSite - not directly available prior to Servlet 6; add via header for strict control
        res.addCookie(cookie);
        // Set SameSite=Lax for localhost dev
        res.addHeader("Set-Cookie",
                "access_token=" + token + "; HttpOnly; Path=/; Max-Age=" + maxAgeSeconds + "; SameSite=Lax" + (secure ? "; Secure" : ""));
    }

    public static void clearAccessTokenCookie(HttpServletResponse res, boolean secure) {
        res.addHeader("Set-Cookie",
                "access_token=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax" + (secure ? "; Secure" : ""));
    }
}