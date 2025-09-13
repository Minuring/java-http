package com.techcourse.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpStatusCode;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("GET /register 요청 시 register.html을 반환한다.")
    void register() throws Exception {
        // given
        final var controller = new RegisterController();

        final var get_register_http11 = new RequestLine(HttpMethod.GET, "/register", "HTTP/1.1");
        final var header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_register_http11, header);

        // when
        final var result = controller.service(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/register.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(result.getHeader().get("content-type")).startsWith("text/html"),
                () -> assertThat(result.getHeader().get("content-length")).isEqualTo(
                        content.getBytes(UTF_8).length + ""),
                () -> assertThat(result.getBody()).isEqualTo(content)
        );
    }

    @Test
    @DisplayName("회원가입 성공 시 302 Found와 함께 index.html로 redirect한다.")
    void register_success() throws Exception {
        // given
        final var controller = new RegisterController();

        final var formRequestBody = "account=popo&password=password&email=popo@gmail.com";
        final var post_register_http11 = new RequestLine(HttpMethod.POST, "/register", "HTTP/1.1");
        final var header = new HttpHeader(Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", formRequestBody.getBytes().length + ""
        ));
        final var request = new HttpRequest(post_register_http11, header, formRequestBody);

        // when
        final var result = controller.service(request);

        // then

        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeader().get("location")).isEqualTo("/index.html")
        );
    }
}
