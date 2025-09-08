package com.techcourse.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.message.HttpRequest;

public class ResourceHandler {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            ".html", "text/html",
            ".css", "text/css",
            ".js", "application/javascript"
    );

    public String handle(final HttpRequest request) throws IOException {
        final var uri = request.startLine().uri();
        final var resource = getClass().getClassLoader().getResource("static" + uri);
        final var file = new File(resource.getFile());

        if (file.exists() && !file.isDirectory()) {
            final var contentType = CONTENT_TYPE_MAP.getOrDefault(getExtension(uri), "text/html");
            final var body = Files.readString(file.toPath());
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body
            );
        }

        throw new IllegalStateException("Resource not found about URI: " + uri);
    }

    private String getExtension(final String uri) {
        if (uri.lastIndexOf('.') == -1) {
            return "";
        }
        return uri.substring(uri.lastIndexOf('.'));
    }
}
