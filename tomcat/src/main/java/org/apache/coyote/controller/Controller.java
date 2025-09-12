package org.apache.coyote.controller;

import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request) throws Exception;
}
