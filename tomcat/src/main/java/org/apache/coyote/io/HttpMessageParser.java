package org.apache.coyote.io;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.message.HttpCookie;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.RequestLine;

public class HttpMessageParser {

    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    public RequestLine parseStartLine(final String startLine) {
        Objects.requireNonNull(startLine);
        final var split = startLine.split(BLANK);
        assert split.length >= 3;

        final var httpMethod = HttpMethod.fromString(split[0].trim());
        final var uri = split[1].trim();
        final var httpVersion = split[2].trim();
        return new RequestLine(httpMethod, uri, httpVersion);
    }

    public HttpHeader parseHeader(final String headers) {
        Objects.requireNonNull(headers);

        final var headersMap = Arrays.stream(headers.split(CRLF))
                .map(header -> header.split(":", 2))
                .collect(toMap(kv -> kv[0].toLowerCase().trim(), kv -> kv[1].toLowerCase().trim()));

        if (headersMap.containsKey("cookie")) {
            final var cookie = parseCookie(headersMap.get("cookie"));
            return new HttpHeader(headersMap, cookie);
        }
        return new HttpHeader(headersMap);
    }

    private HttpCookie parseCookie(final String rawCookieValues) {
        final var cookieMap = Arrays.stream(rawCookieValues.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(toMap(kv -> kv[0].toLowerCase().trim(), kv -> kv[1].toLowerCase().trim()));

        return new HttpCookie(cookieMap);
    }
}
