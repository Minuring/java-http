package org.apache.coyote.message;

import jakarta.servlet.http.HttpSession;
import java.util.Objects;

public record HttpRequest(
        RequestLine requestLine,
        HttpHeader header,
        String body
) {

    public HttpRequest(final RequestLine requestLine, final HttpHeader header, final String body) {
        this.requestLine = Objects.requireNonNull(requestLine);
        this.header = Objects.requireNonNull(header);
        this.body = Objects.requireNonNull(body);
    }

    public HttpRequest(final RequestLine requestLine, final HttpHeader header) {
        this(requestLine, header, "");
    }

    public HttpSession getSession() {
        return header.getCookie().getSession();
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }
}
