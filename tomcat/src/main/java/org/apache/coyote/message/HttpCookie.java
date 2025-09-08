package org.apache.coyote.message;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpSession getSession() {
        if (has(JSESSIONID)) {
            final var sessionId = get(JSESSIONID);
            return SessionManager.INSTANCE.findSession(sessionId);
        }
        return new Session();
    }

    public boolean has(final String name) {
        return cookies.containsKey(name);
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
