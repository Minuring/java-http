package org.apache.coyote.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMethodTest {

    @ParameterizedTest
    @CsvSource({
            "GET,GET",
            "HEAD,HEAD",
            "POST,POST",
            "PUT,PUT",
            "DELETE,DELETE",
            "TRACE,TRACE",
            "OPTIONS,OPTIONS",
            "PATCH,PATCH"
    })
    @DisplayName("문자열로 HTTP Method enum을 얻는다.")
    void fromString(final String string, final HttpMethod httpMethod) {
        final var actual = HttpMethod.fromString(string);
        assertThat(actual).isEqualTo(httpMethod);
    }
}
