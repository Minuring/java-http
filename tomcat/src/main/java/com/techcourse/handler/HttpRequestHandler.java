package com.techcourse.handler;

import java.io.IOException;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public interface HttpRequestHandler {
    HttpResponse handle(HttpRequest request) throws IOException;
}
