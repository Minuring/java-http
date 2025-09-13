package com.techcourse.controller;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.HttpStatusCode;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final var body = new StaticResource("register.html").readAsString();
        return HttpResponse.ok()
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        final var formData = parseBodyAsForm(request);
        if (isValidForm(formData)) {
            register(formData);
            return HttpResponse.builder(HttpStatusCode.FOUND)
                    .location("/index.html")
                    .build();
        }

        return HttpResponse.badRequest()
                .body("계정 또는 비밀번호 또는 이메일이 올바르지 않습니다.")
                .build();
    }

    private void register(final Map<String, String> formData) {
        final var account = formData.get("account");
        final var password = formData.get("password");
        final var email = formData.get("email");

        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private Map<String, String> parseBodyAsForm(final HttpRequest request) {
        return Arrays.stream(request.body().split("&"))
                .map(s -> s.split("="))
                .collect(toMap(kv -> kv[0], kv -> kv[1]));
    }

    private boolean isValidForm(final Map<String, String> form) {
        return form.containsKey("account")
                && form.containsKey("email")
                && form.containsKey("password");
    }
}
