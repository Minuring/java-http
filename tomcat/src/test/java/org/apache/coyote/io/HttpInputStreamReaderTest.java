package org.apache.coyote.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpInputStreamReaderTest {

    @Test
    @DisplayName("InputStream으로부터 HTTP 요청 라인을 읽는다.")
    void readStartLine() throws IOException {
        // given
        final var httpMessage = "GET /index.html HTTP/1.1\r\nContent-Length: 10\r\n\r\nhi";
        final var inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final var httpInputStream = new HttpInputStreamReader(inputStream);

        // when
        final var startLine = httpInputStream.readStartLine();

        // then
        assertThat(startLine).isEqualTo("GET /index.html HTTP/1.1");
    }

    @Test
    @DisplayName("InputStream으로부터 HTTP 요청 헤더를 읽는다.")
    void readHeader() throws IOException {
        // given
        final var httpMessage = "GET /index.html HTTP/1.1\r\nContent-Length: 10\r\nCookie: a=1;";
        final var inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final var httpInputStream = new HttpInputStreamReader(inputStream);

        // when
        final var header = httpInputStream.readHeader();

        // then
        assertThat(header).isEqualTo("Content-Length: 10\r\nCookie: a=1;");
    }

    @Test
    @DisplayName("InputStream으로부터 HTTP 요청 본문을 읽는다.")
    void readBody() throws IOException {
        // given
        final var httpMessage = "GET /index.html HTTP/1.1\r\nContent-Length: 10\r\n\r\nHello world!";
        final var inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final var httpInputStream = new HttpInputStreamReader(inputStream);

        // when
        final var body = httpInputStream.readBody();

        // then
        assertThat(body).isEqualTo("Hello world!");
    }

    @Test
    @DisplayName("HTTP 요청 본문이 없는데 읽으려 하면 빈 문자열을 반환한다.")
    void readBody2() throws IOException {
        // given
        final var httpMessage = "GET /index.html HTTP/1.1\r\nContent-Length: 10";
        final var inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final var httpInputStream = new HttpInputStreamReader(inputStream);

        // when
        final var body = httpInputStream.readBody();

        // then
        assertThat(body).isEmpty();
    }
}
