package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private final Map<String, String> cookies;

    public HttpCookies() {
        this.cookies = new HashMap<>();
    }

    public HttpCookies(final String rawCookieHeader) {
        validateHeaderName(rawCookieHeader);

        final var beginIndex = "Cookie: ".length();
        final var rawCookies = rawCookieHeader.substring(beginIndex).trim();

        this.cookies = Arrays.stream(rawCookies.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(toMap(kv -> kv[0].trim(), kv -> kv[1].trim()));
    }

    private void validateHeaderName(final String rawCookieHeader) {
        if (!rawCookieHeader.startsWith("Cookie: ")) {
            throw new IllegalArgumentException("올바른 쿠키 헤더가 아닙니다 : " + rawCookieHeader);
        }
    }

    public boolean hasCookie(final String name) {
        return cookies.containsKey(name);
    }
}
