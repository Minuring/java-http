package com.techcourse.handler;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.message.HttpRequest;

public class HomeHandler implements HttpRequestHandler {

    public String handle(final HttpRequest request) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + "Hello world!".getBytes(StandardCharsets.UTF_8).length,
                "",
                "Hello world!"
        );
    }
}
