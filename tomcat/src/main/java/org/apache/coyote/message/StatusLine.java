package org.apache.coyote.message;

public record StatusLine(
        String httpVersion,
        HttpStatusCode statusCode,
        String message
) {

}
