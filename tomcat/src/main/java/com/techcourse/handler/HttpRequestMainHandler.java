package com.techcourse.handler;

import java.io.IOException;
import org.apache.coyote.message.HttpRequest;

public class HttpRequestMainHandler {

    private final HomeHandler homeHandler = new HomeHandler();
    private final LoginHandler loginHandler = new LoginHandler();
    private final RegisterHandler registerHandler = new RegisterHandler();
    private final ResourceHandler resourceHandler = new ResourceHandler();

    public String handle(final HttpRequest request) throws IOException {
        final var uri = request.requestLine().uri();

        if (uri.isEmpty() || uri.equals("/")) {
            return homeHandler.handle(request);
        }

        if (uri.startsWith("/login")) {
            return loginHandler.handle(request);
        }

        if (uri.startsWith("/register")) {
            return registerHandler.handle(request);
        }

        final var resource = getClass().getClassLoader().getResource("static" + uri);
        if (resource != null) {
            return resourceHandler.handle(request);
        }

        return "HTTP/1.1 404 Not Found\r\nContent-Length: 0";
    }
}
