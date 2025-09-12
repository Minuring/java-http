package com.techcourse.controller;

import java.net.URL;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class HttpRequestRouter {

    private final RequestMapping requestMapping = new RequestMapping(new NotFoundController());

    public HttpRequestRouter() {
        requestMapping.add(req -> req.requestLine().uri().isEmpty() || req.requestLine().uri().equals("/"), new HomeController());
        requestMapping.add(req -> req.requestLine().uri().startsWith("/login"), new LoginController());
        requestMapping.add(req -> req.requestLine().uri().startsWith("/register"), new RegisterController());
        requestMapping.add(req -> getResource(req.requestLine().uri()) != null, new ResourceController());
    }

    public HttpResponse route(final HttpRequest request) {
        final var controller = requestMapping.getController(request);
        try {
            return controller.service(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getResource(final String uri) {
        return HttpRequestRouter.class.getClassLoader().getResource("static" + uri);
    }
}
