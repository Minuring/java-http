package org.apache.coyote.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceTest {

    @Test
    @DisplayName("정적 리소스 파일을 문자열로 읽는다.")
    void readAsString() throws IOException {
        // given
        final var resource = new StaticResource("js/scripts.js");

        // when
        final var actual = resource.readAsString();

        // then
        final var expectedResource = getClass().getClassLoader().getResource("static/js/scripts.js");
        final var expected = Files.readString(Path.of(expectedResource.getPath()));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("정적 리소스 파일의 확장자를 알 수 있다.")
    void getExtension() throws IOException {
        // given
        final var resource = new StaticResource("index.html");

        // when
        final var actual = resource.getExtension();

        // then
        assertThat(actual).isEqualTo("html");
    }
}
