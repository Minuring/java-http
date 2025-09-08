package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session implements HttpSession {

    private final UUID id = UUID.randomUUID();
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(final String name) {
        return getAttribute(name);
    }

    @Override
    public String[] getValueNames() {
        return attributes.keySet().toArray(String[]::new);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        if (value == null) {
            removeAttribute(name);
            return;
        }

        attributes.merge(name, value, (_old, _new) -> _new);
    }

    @Override
    public void putValue(final String name, final Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    @Override
    public void removeValue(final String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        SessionManager.INSTANCE.remove(this);
        attributes.clear();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }
}
