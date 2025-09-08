package org.apache.coyote.message;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public String getSessionId() {
        return get(JSESSIONID);
    }

    public boolean has(final String name) {
        return cookies.containsKey(name);
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
