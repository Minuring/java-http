package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.message.HttpHeader;
import org.apache.coyote.message.HttpMethod;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpStatusCode;
import org.apache.coyote.message.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    @DisplayName("200 OK와 함께 본문으로 'Hello world!'를 반환한다.")
    void service() throws Exception {
        // given
        final var controller = new HomeController();

        final var get_home_http11 = new RequestLine(HttpMethod.GET, "/", "HTTP/1.1");
        final var empty_header = new HttpHeader(Map.of());
        final var request = new HttpRequest(get_home_http11, empty_header);

        // when
        final var result = controller.service(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusLine().statusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(result.getBody()).isEqualTo("Hello world!")
        );
    }
}
