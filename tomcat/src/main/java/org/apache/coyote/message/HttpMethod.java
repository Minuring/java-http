package org.apache.coyote.message;

import java.util.Arrays;

public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH;

    public boolean isGet() {
        return this == GET;
    }

    public boolean isHead() {
        return this == HEAD;
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isPut() {
        return this == PUT;
    }

    public boolean isDelete() {
        return this == DELETE;
    }

    public boolean isTrace() {
        return this == TRACE;
    }

    public boolean isOptions() {
        return this == OPTIONS;
    }

    public boolean isPatch() {
        return this == PATCH;
    }

    public static HttpMethod fromString(final String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findAny()
                .orElseThrow();
    }
}
