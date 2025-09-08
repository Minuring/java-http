package org.apache.coyote.message;

import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public HttpHeader(final Map<String, String> headers, final HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
        this.cookie = new HttpCookie();
    }

    public boolean has(final String name) {
        return headers.containsKey(name);
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
