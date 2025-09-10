package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestMainHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "a", "/abc", "/a.bc", "/index.html2"
    })
    void notFound(final String uri) throws IOException {
        // given
        final var handler = new HttpRequestMainHandler();

        final var get_http11 = new RequestLine(HttpMethod.GET, uri, "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        assertThat(result).containsSubsequence(
                "HTTP/1.1 404 Not Found",
                "Content-Length: 0"
        );
    }
}
