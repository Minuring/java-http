package org.apache.coyote.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "jsessionid";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final Map<String, String> cookies) {
        cookies.forEach((k, v) -> this.cookies.put(k.toLowerCase(), v));
    }

    public HttpCookie() {
    }

    public String getSessionId() {
        return get(JSESSIONID);
    }

    public String get(final String name) {
        return cookies.get(name.toLowerCase());
    }

    public Map<String, String> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
