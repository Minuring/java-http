package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class HomeController extends AbstractController {

    @Override
    public HttpResponse doGet(final HttpRequest request) {
        final var body = "Hello world!";

        return HttpResponse.ok()
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }
}
