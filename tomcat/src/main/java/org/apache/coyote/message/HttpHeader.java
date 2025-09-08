package org.apache.coyote.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeader {

    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie cookie;

    public HttpHeader(final Map<String, String> headers, final HttpCookie cookie) {
        Objects.requireNonNull(headers);
        Objects.requireNonNull(cookie);

        headers.forEach((k, v) -> this.headers.put(k.toLowerCase(), v.toLowerCase()));
        this.cookie = cookie;
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

    public HttpCookie getCookie() {
        return cookie;
    }
}
