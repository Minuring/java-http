package org.apache.coyote.message;

public record RequestLine(
        HttpMethod httpMethod,
        String uri,
        String httpVersion
) {

}
