package org.apache.coyote.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeader {

    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie cookie;

    public HttpHeader(final Map<String, String> headers, final HttpCookie cookie) {
        if (headers != null) {
            headers.forEach((k, v) -> this.headers.put(k.toLowerCase(), v.toLowerCase()));
        }

        this.cookie = Objects.requireNonNullElse(cookie, new HttpCookie());
    }

    public HttpHeader(final Map<String, String> headers) {
        this(headers, new HttpCookie());
    }

    public boolean has(final String name) {
        return headers.containsKey(name.toLowerCase());
    }

    public String get(final String name) {
        return headers.get(name.toLowerCase());
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
