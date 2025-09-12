package org.apache.coyote.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeader header;
    private final String body;

    private HttpResponse(final StatusLine statusLine, final HttpHeader header, final String body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public static Builder builder(final HttpStatusCode code) {
        return new Builder(new StatusLine("HTTP/1.1", code, code.getMessage()));
    }

    public static Builder ok() {
        return builder(HttpStatusCode.OK);
    }

    public static Builder notFound() {
        final var builder = builder(HttpStatusCode.NOT_FOUND);
        return builder.contentLength(0);
    }

    public static class Builder {

        private final StatusLine statusLine;
        private Map<String, String> headers;
        private Map<String, String> cookies;
        private String body;

        public Builder(final StatusLine statusLine) {
            this.statusLine = statusLine;
            this.headers = new LinkedHashMap<>();
            this.cookies = new LinkedHashMap<>();
            this.body = "";
        }

        public Builder header(final String name, final String value) {
            headers.merge(name, value, (_old, _new) -> _new);
            return this;
        }

        public Builder location(final String location) {
            return header("location", location);
        }

        public Builder addCookie(final String name, final String value) {
            cookies.merge(name, value, (_old, _new) -> _new);
            return this;
        }

        public Builder contentType(final String contentType) {
            return header("content-type", contentType);
        }

        public Builder contentLength(final int contentLength) {
            return header("content-length", String.valueOf(contentLength));
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, new HttpHeader(headers, new HttpCookie(cookies)), body);
        }
    }
}
