package org.apache.coyote.message;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.catalina.session.SessionManager;

public class HttpCookie {

    private static final String JSESSIONID = "jsessionid";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final Map<String, String> cookies) {
        cookies.forEach((k, v) -> this.cookies.put(k.toLowerCase(), v.toLowerCase()));
    }

    public HttpCookie() {
    }

    public HttpSession getSession() {
        return SessionManager.INSTANCE.findSession(get(JSESSIONID));
    }

    public String get(final String name) {
        return cookies.get(name.toLowerCase());
    }
}
