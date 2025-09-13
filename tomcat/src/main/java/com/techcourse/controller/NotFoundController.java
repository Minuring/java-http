package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public HttpResponse service(final HttpRequest request) {
        return HttpResponse.notFound().build();
    }
}
