package org.apache.coyote.io;

import static java.util.stream.Collectors.joining;

import org.apache.coyote.message.HttpCookie;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.StatusLine;

public class HttpResponseEncoder {

    private static final String CRLF = "\r\n";

    public String encode(final HttpResponse response) {
        final var statusLine = encodeStatusLine(response.getStatusLine());
        final var header = encodeHeader(response.getHeader());
        final var body = response.getBody();

        final var message = statusLine + CRLF + header + CRLF + CRLF;
        if (body != null && !body.isEmpty()) {
            return message + body;
        }

        return message;
    }

    private String encodeStatusLine(final StatusLine statusLine) {
        return String.format("%s %d %s",
                statusLine.httpVersion(),
                statusLine.statusCode().getCode(),
                statusLine.statusCode().getMessage()
        );
    }

    private static String encodeHeader(final HttpHeader header) {
        final var headers = header.getHeaders().entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(joining(CRLF));

        final var cookieHeader = encodeCookieHeader(header.getCookie());
        if (cookieHeader.isEmpty()) {
            return headers;
        }
        return String.join(CRLF, headers, cookieHeader);
    }

    private static String encodeCookieHeader(final HttpCookie cookie) {
        final var cookies = cookie.getCookies();
        if (cookies == null || cookies.isEmpty()) {
            return "";
        }

        final var value = cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(joining(";"));
        return String.format("set-cookie: %s%s", value, CRLF);
    }
}
