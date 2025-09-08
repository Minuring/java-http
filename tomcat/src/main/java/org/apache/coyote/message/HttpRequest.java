package org.apache.coyote.message;

import jakarta.servlet.http.HttpSession;
import java.util.Objects;

public record HttpRequest(
        StartLine startLine,
        HttpHeader header,
        String body
) {

    public HttpRequest(final StartLine startLine, final HttpHeader header, final String body) {
        this.startLine = Objects.requireNonNull(startLine);
        this.header = Objects.requireNonNull(header);
        this.body = Objects.requireNonNull(body);
    }

    public HttpRequest(final StartLine startLine, final HttpHeader header) {
        this(startLine, header, "");
    }

    public HttpSession getSession() {
        return header.getCookie().getSession();
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }
}
