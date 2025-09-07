package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a", "/abc", "/a.bc", "/index.html2"
    })
    void notFound(String uri) {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = "HTTP/1.1 404 Not Found \r\nContent-Length: 0";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final var content = Files.readString(Path.of(resource.getPath()));
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "\r\n" +
                content;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/js/scripts.js",
            "/assets/chart-area.js",
            "/assets/chart-bar.js",
            "/assets/chart-pie.js",
    })
    void js(final String path) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: application/javascript,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var resource = getClass().getClassLoader().getResource("static" + path);
        final var content = Files.readString(Path.of(resource.getPath()));
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: application/javascript;charset=utf-8 \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "\r\n" +
                content;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var resource = getClass().getClassLoader().getResource("static/login.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "\r\n" +
                content;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 성공 시 302 Found와 함께 index.html로 redirect한다.")
    void login_success() {
        // given
        final var formRequestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + formRequestBody.getBytes().length,
                "",
                formRequestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then

        assertThat(socket.output()).containsSequence(
                "HTTP/1.1 302 Found \r\n",
                "Location: /index.html"
        );
    }

    @Test
    @DisplayName("로그인 성공 시 Set-Cookie 헤더에 JSESSIONID를 포함한다.")
    void login_success_setCookie() {
        // given
        final var formRequestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + formRequestBody.getBytes().length,
                "",
                formRequestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then

        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("로그인 실패 시 302 Found와 함께 401.html로 redirect한다.")
    void login_fail() {
        // given
        final var formRequestBody = "account=abcd&password=dddd";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + formRequestBody.getBytes().length,
                "",
                formRequestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var resource = getClass().getClassLoader().getResource("static/register.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        final var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + content.getBytes().length + " \r\n" +
                "\r\n" +
                content;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 성공 시 302 Found와 함께 index.html로 redirect한다.")
    void register_success() {
        // given
        final var formRequestBody = "account=popo&password=password&email=popo@gmail.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + formRequestBody.getBytes().length,
                "",
                formRequestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
