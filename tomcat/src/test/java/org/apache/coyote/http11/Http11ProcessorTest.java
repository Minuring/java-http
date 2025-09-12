package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
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
        final var output = socket.output();
        assertAll(
                () -> assertThat(output).containsIgnoringCase("HTTP/1.1 200 OK"),
                () -> assertThat(output).containsIgnoringCase("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).containsIgnoringCase("Content-Length: 12"),
                () -> assertThat(output).containsOnlyOnce("Hello world!")
        );
    }
}
