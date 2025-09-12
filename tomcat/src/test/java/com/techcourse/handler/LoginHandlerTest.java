package com.techcourse.handler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.message.HttpCookie;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpStatusCode;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("GET /login 요청 시 login.html을 반환한다.")
    void login() throws IOException {
        // given
        final var handler = new LoginHandler();

        final var get_login_http11 = new RequestLine(HttpMethod.GET, "/login", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_login_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        final var resource = getClass().getClassLoader().getResource("static/login.html");
        final var content = Files.readString(Path.of(resource.getPath()));
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(result.getHeader().get("content-type")).startsWith("text/html"),
                () -> assertThat(result.getHeader().get("content-length")).isEqualTo(content.getBytes(UTF_8).length + ""),
                () -> assertThat(result.getBody()).isEqualTo(content)
        );
    }

    @Test
    @DisplayName("GET /login 요청 시 이미 로그인되어 있으면 index.html로 redirect한다.")
    void login_redirect() throws IOException {
        // given
        final var handler = new LoginHandler();

        final var session = new Session();
        session.setAttribute("user", new User("gugu", "password", "email@email.com"));
        SessionManager.INSTANCE.add(session);

        final var get_login_http11 = new RequestLine(HttpMethod.GET, "/login", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of(), new HttpCookie(Map.of("JSESSIONID", session.getId())));
        final var request = new HttpRequest(get_login_http11, empty_header);

        // when
        final var result = handler.handle(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeader().get("location")).isEqualTo("/index.html")
        );
    }

    @Test
    @DisplayName("로그인 성공 시 302 Found와 함께 index.html로 redirect한다.")
    void login_success() throws IOException {
        // given
        final var handler = new LoginHandler();

        final var formRequestBody = "account=gugu&password=password";
        final var post_login_http11 = new RequestLine(HttpMethod.POST, "/login", "HTTP/1.1");
        final var header = new HttpHeader(Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", formRequestBody.getBytes().length + ""
        ));
        final var request = new HttpRequest(post_login_http11, header, formRequestBody);

        // when
        final var result = handler.handle(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeader().get("location")).isEqualTo("/index.html")
        );
    }

    @Test
    @DisplayName("로그인 성공 시 Set-Cookie 헤더에 JSESSIONID를 포함한다.")
    void login_success_set_cookie() throws IOException {
        // given
        final var handler = new LoginHandler();

        final var formRequestBody = "account=gugu&password=password";
        final var post_login_http11 = new RequestLine(HttpMethod.POST, "/login", "HTTP/1.1");
        final var header = new HttpHeader(Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", formRequestBody.getBytes().length + ""
        ));
        final var request = new HttpRequest(post_login_http11, header, formRequestBody);

        // when
        final var result = handler.handle(request);

        // then
        assertThat(result.getHeader().getCookie().getSessionId()).isNotEmpty();
    }

    @Test
    @DisplayName("로그인 실패 시 302 Found와 함께 401.html로 redirect한다.")
    void login_fail() throws IOException {
        // given
        final var handler = new LoginHandler();

        final var formRequestBody = "account=abcd&password=defg";
        final var post_login_http11 = new RequestLine(HttpMethod.POST, "/login", "HTTP/1.1");
        final var header = new HttpHeader(Map.of(
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", formRequestBody.getBytes().length + ""
        ));
        final var request = new HttpRequest(post_login_http11, header, formRequestBody);

        // when
        final var result = handler.handle(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeader().get("location")).isEqualTo("/401.html")
        );
    }
}
