package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeHandlerTest {

    @Test
    @DisplayName("문자열 'Hello world!'를 반환한다.")
    void handle() {
        // given
        final var handler = new HomeHandler();

        final var get_home_http11 = new RequestLine(HttpMethod.GET, "/", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_home_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then

        assertThat(result).containsSubsequence(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "\r\n\r\n",
                "Hello world!"
        );
    }
}
