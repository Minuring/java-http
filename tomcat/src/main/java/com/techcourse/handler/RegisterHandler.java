package com.techcourse.handler;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.HttpStatusCode;

public class RegisterHandler implements HttpRequestHandler {

    public HttpResponse handle(final HttpRequest request) throws IOException {
        final var httpMethod = request.requestLine().httpMethod();

        if (httpMethod.isGet()) {
            final var body = new StaticResource("register.html").readAsString();
            return HttpResponse.ok()
                    .contentType("text/html;charset=utf-8")
                    .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                    .body(body)
                    .build();
        }

        if (httpMethod.isPost()) {
            final var formData = Arrays.stream(request.body().split("&"))
                    .map(s -> s.split("="))
                    .collect(toMap(kv -> kv[0], kv -> kv[1]));

            register(formData);
            return HttpResponse.builder(HttpStatusCode.FOUND)
                    .location("/index.html")
                    .build();
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다.");
    }

    private void register(final Map<String, String> formData) {
        final var account = formData.get("account");
        final var password = formData.get("password");
        final var email = formData.get("email");
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
