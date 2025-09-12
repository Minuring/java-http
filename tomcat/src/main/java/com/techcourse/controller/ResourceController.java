package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class ResourceController extends AbstractController {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript"
    );

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final var uri = request.requestLine().uri();
        final var resource = new StaticResource(uri);

        final var contentType = CONTENT_TYPE_MAP.getOrDefault(resource.getExtension(), "text/html");
        final var body = resource.readAsString();
        return HttpResponse.ok()
                .contentType(contentType)
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }
}
