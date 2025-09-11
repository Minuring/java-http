package org.apache.coyote.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void getSession() {
        // given
        final var session = new Session();
        session.setAttribute("test", 1);
        SessionManager.INSTANCE.add(session);

        final var cookie = new HttpCookie(Map.of("JSESSIONID", session.getId()));

        // when
        final var actual = cookie.getSession();

        // then
        assertThat(actual).isEqualTo(session);
        assertThat(actual.getAttribute("test")).isEqualTo(1);
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
