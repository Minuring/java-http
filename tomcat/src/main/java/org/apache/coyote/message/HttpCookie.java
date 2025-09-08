package org.apache.coyote.message;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
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
        return SessionManager.INSTANCE.findSession(get(JSESSIONID));
    }

    public String get(final String name) {
        return cookies.get(name);
    }
}
