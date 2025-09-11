package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceHandlerTest {

    @Test
    @DisplayName("GET /index.html 요청 시 Content-Type: text/html로 반환한다.")
    void index() throws IOException {
        // given
        final var handler = new ResourceHandler();

        final var get_indexhtml_http11 = new RequestLine(HttpMethod.GET, "/index.html", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_indexhtml_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/index.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        assertThat(result).containsSubsequence(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "Content-Length: " + content.getBytes().length,
                "\r\n\r\n",
                content
        );
    }

    @Test
    @DisplayName("GET /styles.css 요청 시 Content-Type: text/css로 반환한다.")
    void css() throws IOException {
        // given
        final var handler = new ResourceHandler();

        final var get_stylescss_http11 = new RequestLine(HttpMethod.GET, "/css/styles.css", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_stylescss_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final var content = Files.readString(Path.of(resource.getPath()));
        assertThat(result).containsSubsequence(
                "HTTP/1.1 200 OK",
                "Content-Type: text/css",
                "Content-Length: " + content.getBytes().length,
                "\r\n\r\n",
                content
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/js/scripts.js",
            "/assets/chart-area.js",
            "/assets/chart-bar.js",
            "/assets/chart-pie.js",
    })
    @DisplayName("GET /~.js 요청 시 Content-Type: application/javascript로 반환한다.")
    void js(final String path) throws IOException {
        // given
        final var handler = new ResourceHandler();

        final var get_path_http11 = new RequestLine(HttpMethod.GET, path, "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_path_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static" + path);
        final var content = Files.readString(Path.of(resource.getPath()));
        assertThat(result).containsSubsequence(
                "HTTP/1.1 200 OK",
                "Content-Type: application/javascript",
                "Content-Length: " + content.getBytes().length,
                "\r\n\r\n",
                content
        );
    }
}
