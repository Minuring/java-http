package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.StartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterHandlerTest {

    @Test
    @DisplayName("GET /register 요청 시 register.html을 반환한다.")
    void register() throws IOException {
        // given
        final var handler = new RegisterHandler();

        final var get_register_http11 = new StartLine(HttpMethod.GET, "/register", "HTTP/1.1");
        final var header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_register_http11, header);

        // when
        final var result = handler.handle(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/register.html");
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
    @DisplayName("회원가입 성공 시 302 Found와 함께 index.html로 redirect한다.")
    void register_success() throws IOException {
        // given
        final var handler = new RegisterHandler();

        final var formRequestBody = "account=popo&password=password&email=popo@gmail.com";
        final var post_register_http11 = new StartLine(HttpMethod.POST, "/register", "HTTP/1.1");
        final var header = new HttpHeader(Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", formRequestBody.getBytes().length + ""
        ));
        final var request = new HttpRequest(post_register_http11, header, formRequestBody);

        // when
        final var result = handler.handle(request);

        // then

        assertThat(result).containsSubsequence(
                "HTTP/1.1 302 Found",
                "Location: /index.html"
        );
    }
}
