package org.apache.coyote.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void getSessionId() {
        // given
        final var sessionId = new Session().getId();
        final var cookie = new HttpCookie(Map.of("JSESSIONID", sessionId));

        // when
        final var actual = cookie.getSessionId();

        // then
        assertThat(actual).isEqualTo(sessionId);
    }

    @Test
    void get() {
        // given
        final var cookie = new HttpCookie(Map.of("test", "abc"));

        // when
        final var actual = cookie.get("test");

        // then
        assertThat(actual).isEqualTo("abc");
    }
}
