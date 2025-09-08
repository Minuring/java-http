package com.techcourse.handler;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import org.apache.coyote.message.HttpRequest;

public class RegisterHandler {

    public String handle(final HttpRequest request) throws IOException {
        final var httpMethod = request.startLine().httpMethod();

        if (httpMethod.isGet()) {
            final var resource = getClass().getClassLoader().getResource("static/register.html");
            final var file = new File(resource.getPath());
            final var body = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body
            );
        }

        if (httpMethod.isPost()) {
            final var formData = Arrays.stream(request.body().split("&"))
                    .map(s -> s.split("="))
                    .collect(toMap(kv -> kv[0], kv -> kv[1]));

            final var account = formData.get("account");
            final var password = formData.get("password");
            final var email = formData.get("email");
            final var user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            return "HTTP/1.1 302 Found \r\nLocation: /index.html";
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다.");
    }
}
