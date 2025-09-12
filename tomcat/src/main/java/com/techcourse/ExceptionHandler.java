package com.techcourse;

import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    public HttpResponse handle(final HttpRequest request, final Exception e) {
        log.error(e.getMessage(), e);
        final var message = "서버에서 예상하지 못한 오류가 발생했습니다.";

        return HttpResponse.internalServerError()
                .body(message)
                .build();
    }
}
