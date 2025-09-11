package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.HttpRequestRouter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.io.HttpInputStreamReader;
import org.apache.coyote.io.HttpMessageParser;
import org.apache.coyote.message.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var httpRequest = readMessage(inputStream);
            final var handler = new HttpRequestRouter();
            final var response = handler.route(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readMessage(final InputStream inputStream) throws IOException {
        final var reader = new HttpInputStreamReader(inputStream);
        final var parser = new HttpMessageParser();

        final var requestLine = parser.parseRequestLine(reader.readRequestLine());
        final var header = parser.parseHeader(reader.readHeader());
        final var body = reader.readBody();

        return new HttpRequest(requestLine, header, body);
    }
}
