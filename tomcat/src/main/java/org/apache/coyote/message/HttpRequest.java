package org.apache.coyote.message;

import java.util.Objects;

public record HttpRequest(
        RequestLine requestLine,
        HttpHeader header,
        String body
) {

    public HttpRequest(final RequestLine requestLine, final HttpHeader header, final String body) {
        this.requestLine = Objects.requireNonNull(requestLine, "request line cannot be null");
        this.header = Objects.requireNonNull(header, "header cannot be null");
        this.body = Objects.requireNonNull(body, "body cannot be null");
    }

    public HttpRequest(final RequestLine requestLine, final HttpHeader header) {
        this(requestLine, header, "");
    }

    public String getSessionId() {
        return header.getCookie().getSessionId();
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }
}
