package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        if (id == null || !sessions.containsKey(id)) {
            return new Session();
        }
        return sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        sessions.remove(session.getId());
    }
}
