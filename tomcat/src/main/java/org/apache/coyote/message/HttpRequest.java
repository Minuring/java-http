package org.apache.coyote.message;

public record HttpRequest(
        StartLine startLine,
        HttpHeader header,
        String body
) {

    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }
}
