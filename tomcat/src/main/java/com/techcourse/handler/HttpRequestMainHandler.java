package com.techcourse.handler;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.coyote.message.HttpRequest;

public class HttpRequestMainHandler {

    private static final Map<Predicate<String>, HttpRequestHandler> SEQUENCED_ROUTER;

    static {
        SEQUENCED_ROUTER = new LinkedHashMap<>(Map.ofEntries(
                Map.entry(uri -> uri.isEmpty() || uri.equals("/"), new HomeHandler()),
                Map.entry(uri -> uri.startsWith("/login"), new LoginHandler()),
                Map.entry(uri -> uri.startsWith("/register"), new RegisterHandler()),
                Map.entry(uri -> getResource(uri) != null, new HomeHandler())
        ));
    }

    public String handle(final HttpRequest request) throws IOException {
        final var uri = request.requestLine().uri();

        for (final var entry : SEQUENCED_ROUTER.entrySet()) {
            final var predicate = entry.getKey();
            final var handler = entry.getValue();

            if (predicate.test(uri)) {
                return handler.handle(request);
            }
        }

        return "HTTP/1.1 404 Not Found\r\nContent-Length: 0";
    }

    private static URL getResource(final String uri) {
        return HttpRequestMainHandler.class.getClassLoader().getResource("static" + uri);
    }
}
