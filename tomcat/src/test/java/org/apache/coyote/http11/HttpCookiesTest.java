package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @Test
    @DisplayName("Cookies HTTP 요청 헤더로부터 HttpCookies 객체를 만든다.")
    void constructor() {
        //given
        final var header = "Cookie: a=1; b=2; c=3; d=4;";

        //when
        final var httpCookies = new HttpCookies(header);

        //then
        assertAll(
                () -> assertThat(httpCookies.hasCookie("a")).isTrue(),
                () -> assertThat(httpCookies.hasCookie("b")).isTrue(),
                () -> assertThat(httpCookies.hasCookie("c")).isTrue(),
                () -> assertThat(httpCookies.hasCookie("d")).isTrue()
        );
    }

}
