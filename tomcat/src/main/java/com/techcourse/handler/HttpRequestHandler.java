package com.techcourse.handler;

import java.io.IOException;
import org.apache.coyote.message.HttpRequest;

public interface HttpRequestHandler {
    public String handle(HttpRequest request) throws IOException;
}
