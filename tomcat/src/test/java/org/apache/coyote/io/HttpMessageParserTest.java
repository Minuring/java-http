package org.apache.coyote.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.message.HttpCookie;
import org.apache.coyote.message.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMessageParserTest {

    @Test
    @DisplayName("HTTP 요청 메시지의 시작 줄을 파싱한다.")
    void parseStartLine() {
        // given
        final var parser = new HttpMessageParser();

        // when
        final var startLine = parser.parseStartLine("GET /foo HTTP/1.1");

        // then
        assertAll(
                () -> assertThat(startLine.httpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(startLine.uri()).isEqualTo("/foo"),
                () -> assertThat(startLine.httpVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    @DisplayName("HTTP 요청 메시지의 헤더를 파싱한다.")
    void parseHeader() {
        // given
        final var parser = new HttpMessageParser();

        // when
        final var header = parser.parseHeader("Content-Type: text/plain\r\nCookie: a=1;");

        // then
        assertAll(
                () -> assertThat(header.has("content-type")).isTrue(),
                () -> assertThat(header.get("content-type")).isEqualTo("text/plain"),

                () -> assertThat(header.getCookie()).isInstanceOf(HttpCookie.class),
                () -> assertThat(header.getCookie().get("a")).isEqualTo("1")
        );
    }
}
