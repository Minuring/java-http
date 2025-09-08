package org.apache.coyote.message;

public record StartLine(
        HttpMethod httpMethod,
        String uri,
        String httpVersion
) {

}
