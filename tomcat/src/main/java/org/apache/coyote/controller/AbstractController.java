package org.apache.coyote.controller;

import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        final var httpMethod = request.requestLine().httpMethod();
        return switch (httpMethod) {
            case GET -> doGet(request);
            case HEAD -> doHead(request);
            case OPTIONS -> doOptions(request);
            case POST -> doPost(request);
            case PUT -> doPut(request);
            case DELETE -> doDelete(request);
            case TRACE -> doTrace(request);
            default -> methodNotAllowed();
        };
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doHead(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doPut(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doDelete(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doOptions(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    protected HttpResponse doTrace(final HttpRequest request) throws Exception {
        return methodNotAllowed();
    }

    private HttpResponse methodNotAllowed() {
        return HttpResponse.builder(HttpStatusCode.METHOD_NOT_ALLOWED).build();
    }
}
