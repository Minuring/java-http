package com.techcourse.handler;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.HttpResponse.Builder;
import org.apache.coyote.message.HttpStatusCode;

public class HomeHandler implements HttpRequestHandler {

    public HttpResponse handle(final HttpRequest request) {
        final var body = "Hello world!";

        return HttpResponse.ok()
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }
}
