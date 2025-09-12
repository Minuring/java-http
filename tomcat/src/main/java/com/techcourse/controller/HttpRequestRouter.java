package com.techcourse.controller;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class HttpRequestRouter {

    private static final Map<Predicate<String>, Controller> SEQUENCED_ROUTER;

    static {
        SEQUENCED_ROUTER = new LinkedHashMap<>();
        SEQUENCED_ROUTER.put(uri -> uri.isEmpty() || uri.equals("/"), new HomeController());
        SEQUENCED_ROUTER.put(uri -> uri.startsWith("/login"), new LoginController());
        SEQUENCED_ROUTER.put(uri -> uri.startsWith("/register"), new RegisterController());
        SEQUENCED_ROUTER.put(uri -> getResource(uri) != null, new ResourceController());
    }

    public HttpResponse route(final HttpRequest request) {
        final var uri = request.requestLine().uri();

        for (final var entry : SEQUENCED_ROUTER.entrySet()) {
            final var predicate = entry.getKey();
            final var controller = entry.getValue();
            if (predicate.test(uri)) {
                try {
                    return controller.service(request);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return HttpResponse.notFound().build();
    }

    private static URL getResource(final String uri) {
        return HttpRequestRouter.class.getClassLoader().getResource("static" + uri);
    }
}
